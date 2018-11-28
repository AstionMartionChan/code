# !/usr/bin/env python
# -*-coding: UTF-8 -*-


#**********************程序说明*********************************#
#*模块：实时组的离线计算
#*功能：Spark程序提交
#*作者：zhouml
#*时间：2018-06-19
#***************************************************************#


import re
import subprocess
from subprocess import PIPE
import sys
import os
import time
from kingnetdc import get_hadoop_job_info_by_appid


def main(argv):

    script_name = os.path.basename(__file__)

    if len(argv) < 2:
        raise Exception('no bash path passed')

    proc = subprocess.Popen(['sh', argv[1]], stdout=PIPE, stderr=PIPE)

    stdout, stderr = proc.communicate()

    yarn_application_ids = re.findall(r"application_\d{13}_\d+", stderr.decode('utf-8'))

    if len(yarn_application_ids):

        yarn_application_id = yarn_application_ids[0]

        while True:

            print('Send application status check request for {}'.format(yarn_application_id))

            info = get_hadoop_job_info_by_appid(yarn_application_id)

            status_code = info['code']

            if status_code == 200:
                final_status = info['data']['finalStatus']

                if final_status == 'FAILED' or final_status == 'KILLED':
                    raise Exception('Failed to executed the job in {}'.format(script_name))
                elif final_status == 'SUCCEEDED':
                    print('Job in {} done'.format(script_name))
                    exit(0)
                else:
                    pass

                # 休息10s, 然后再重试
                time.sleep(10)
            else:
                raise Exception('Failed to get status for {}'.format(yarn_application_id))


if __name__ == '__main__':
    main(sys.argv)
