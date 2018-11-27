#!/usr/bin/env python
# -*- coding: UTF-8 -*-
import time
import string
import json
import os
import six
import abc
import base64
import gzip
import hashlib
from urllib import request


class DanaData:
    """The class DanaData Encapsulation to call the passed in class functions. 
    """
    dana_api_version = "2"
    dana_sdk_type = 'python'
    dana_sdk_version = '2.0'
    __consumer = None

    def __init__(self, consumer_cls):
        self.__consumer = consumer_cls

    def send(self, msg_cls):
        try:
            return self.__consumer.send(msg_cls)
        except Exception:
            raise DanaDataException('message is not invalid.')

    def flush(self):
        return self.__consumer.flush()

    def close(self):
        self.__consumer.close()


class DanaMessage:
    """ Build a standard information data structure that meets the definition and perform data check verification.
    """

    def __init__(self, project, event, did='', ouid='', timestamp=0):
        self.__properties = dict()
        self.__check_init_format(project, did, ouid, event)
        self.__properties['properties'] = dict()
        self.__properties['project'] = project
        self.__properties['did'] = did
        self.__properties['ouid'] = ouid
        self.__properties['event'] = event
        if timestamp:
            self.__properties['timestamp'] = int(timestamp)
        else:
            self.__properties['timestamp'] = int(time.time() * 1000)

    def __check_init_format(self, project, did, ouid, event):
        self.__check_value_format([project, did, ouid, event])
        if did in ['', None] and ouid in ['', None]:
            raise DanaDataException("did(设备唯一区分) 或 ouid(用户唯一区分) 不能同时为空")

    @staticmethod
    def __check_value_format(values):
        for value in values:
            if not isinstance(value, (int, float, str)) or len(str(value)) > 255:
                raise DanaDataException("数值不符合规范，数值是简单类型，string, int, float，且长度不能大于255个字符.")

    def __check_property_format(self, key, value):
        check_char = '_' + string.ascii_letters
        if not (isinstance(key, str) and len(key) < 65 and key[0] in check_char):
            raise DanaDataException("属性key不符合规范，必须为字符串，且已'a-zA-Z_'开头，最长为64个字符.")
        self.__check_value_format([value])

    def set_properties(self, li):
        if isinstance(li, (list, tuple)):
            for k, v in li:
                self.set_property(k, v)

    def set_property(self, args_k, args_v):
        self.__check_property_format(args_k, args_v)
        self.__properties['properties'][args_k] = args_v

    def to_json(self):
        return json.dumps(self.__properties)

    def show(self):
        print(self.__properties)

    def get_properties(self):
        return self.__properties


def check_path_writable(file_path):
    """ Check whether the specified path can read and write.
    :return: True or False
    """
    try:
        with open('%st' % file_path, 'w+'):
            pass
        just = True
    except PermissionError:
        just = False
    return just


@six.add_metaclass(abc.ABCMeta)
class Consumer:
    @abc.abstractclassmethod
    def send(self, msg_cls):
        pass

    def flush(self):
        pass

    def close(self):
        pass


class FileConsumer(Consumer):
    """Record information in a file.
    """
    def __init__(self, file_path, file_name_pre='dana'):
        super(Consumer, self).__init__()

        if os.path.isdir(file_path) and check_path_writable(file_path):
            raise DanaDataException("文件保存路径不可写")
        self.__file_path = file_path
        self.__file_name_pre = file_name_pre

    def show(self):
        print([self.__file_path])
        print([self.__file_name_pre])

    def send(self, msg_cls):
        json_str = msg_cls.to_json() + '\r\n'
        file_name = self.__file_path + '/' + self.__file_name_pre + '_' + time.strftime('%Y-%m-%d-%H',
                                                                                        time.localtime()) + '.txt'
        with open(file_name, 'a+') as f:
            f.write(json_str)


class DebugConsumer(Consumer):
    """Standard output to console to display consumed message content.
    """
    def send(self, msg_cls):
        print('send message is : %s \n' % msg_cls.to_json())


class BatchConsumer(Consumer):
    """Batch escalation messages to the log receiving server.
    
    """
    __param = dict()

    def __init__(self, url_str, param_dict=__param):

        super(Consumer, self).__init__()

        if not ('topic' in param_dict and 'token' in param_dict):
            raise DanaDataException('必须要带上参数 topic, token')

        self.__buffers = list()
        self.__url_str = url_str
        self.__max_buffer_size = param_dict.get('maxBufferSize', 10)
        self.__request_timeout = param_dict.get('requestTimeout', 10)
        self.__isGzip = param_dict.get('isGzip', False)
        self.__topic = param_dict['topic']
        self.__key_str = param_dict.get('key', '')
        self.__token = param_dict['token']
        self.__debug = param_dict.get('debug', False)

    def send(self, msg_cls):
        self.__buffers.append(msg_cls.to_json())
        if len(self.__buffers) > self.__max_buffer_size:
            return self.flsh()
        else:
            return True

    def flush(self):
        ret = self.__do_post()
        if ret:
            self.__buffers = list()
        return ret

    def close(self):
        return self.flush()

    def __do_post(self):
        if self.__buffers:
            headers_dict = dict()
            headers_dict['Version'] = DanaData.dana_api_version
            headers_dict['Topic'] = self.__topic
            headers_dict['Key'] = self.__key_str

            post_str = '[' + ','.join(self.__buffers) + ']'

            if self.__isGzip:
                post_str = str(base64.b64encode(gzip.compress(post_str.encode())), 'utf8')
                headers_dict["Content-Type"] = "application/x-gzip; charset=UTF-8"
            else:
                headers_dict["Content-Type"] = "application/json; charset=UTF-8"

            headers_dict['Authorization'] = self.__get_authorization_str(post_str)

            request_obj = request.Request(url=self.__url_str, headers=headers_dict,
                                          data=post_str.encode(), method='POST')
            result = request.urlopen(request_obj, timeout=self.__request_timeout).read()

            if self.__debug:
                print('headers_dict: ', headers_dict)
                print('post_string:', post_str)
                print('ret: ', request_obj.__dict__)
                print('exec ret:', str(result, 'utf8'))

            return str(result, 'utf8') == '0'
        else:
            return True

    def __get_authorization_str(self, body_str):
        """
        :return: string of md5 
        """
        m_str = self.__token + self.__topic + DanaData.dana_api_version + self.__token + body_str
        return hashlib.md5(m_str.encode()).hexdigest()


class DanaDataException(Exception):
    """Custom exception class.
    """
    def __init__(self, err):
        Exception.__init__(self)
        self.err = err

    def __str__(self):
        return self.err
