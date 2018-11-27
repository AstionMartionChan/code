# coding:utf-8
import redis
import rediscluster


class Redis(object):

    _client = None

    def __new__(cls, host, port):
        if not cls._client:
            cls._client = cls.connect(host, port)

        return super(Redis, cls).__new__(cls)

    @staticmethod
    def connect(host, port):
        return redis.Redis(host=host, port=port, decode_responses=True, max_connections=32)

    def call(self, key, method, *args, **kwargs):
        bound_method = getattr(self._client, method)
        return bound_method(key, *args, **kwargs)

    def get(self, key):
        """
        获取string型 数据
        """
        return self._client.get(key)

    def set(self, key, value):
        """
        设置string型数据
        """
        return self._client.set(key, value)

    def delete(self, *args):
        """
        删除给定的一个或多个 key
        """
        return self._client.delete(*args)

    def type(self, key):
        """
        查看key对应的数据类型
        """
        return self._client.type(key)

    def zadd(self, key, value=None, socre=None, **pairs):
        return self._client.zadd(key, value=value, socre=socre, **pairs)


class RedisCluster(object):

    _client = None

    def __new__(cls, nodes):
        if not cls._client:
            cls._client = cls.connect(nodes)

        return super(RedisCluster, cls).__new__(cls)

    @staticmethod
    def connect(nodes):
        """
        
        :param nodes: list; e.g. [{'host':'xxx','port':'xxx'},...]
        :return: a connect
        """
        return rediscluster.StrictRedisCluster(startup_nodes=nodes, decode_responses=True)

    def call(self, key, method, *args, **kwargs):
        bound_method = getattr(self._client, method)
        return bound_method(key, *args, **kwargs)

    def get(self, key):
        """
        获取string型 数据
        """
        return self._client.get(key)

    def set(self, key, value):
        """
        设置string型数据
        """
        return self._client.set(key, value)

    def delete(self, *args):
        """
        删除给定的一个或多个 key
        """
        return self._client.delete(*args)

    def type(self, key):
        """
        查看key对应的数据类型
        """
        return self._client.type(key)

    def zadd(self, key, value=None, socre=None, **pairs):
        return self._client.zadd(key, value=value, socre=socre, **pairs)
