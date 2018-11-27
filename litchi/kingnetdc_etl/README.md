[![Build Status](https://gitlab.ops.kingnet.com/fruits/litchi.git)](https://gitlab.ops.kingnet.com/fruits/litchi.git)

# ETL组Python自定义工具库

## Python 第三方库列表

正常使用必须安装以下Python第三方库
 
 >1. pymysql  
 >2. PyHive  

系统环境依赖  
centos:  
>1. gcc-c++  
>2. cyrus-sasl-devel  
>3. mysql  
>4. python3.6  
>5. python36-devel  
>7. hiveserver2  
>8. hadoop   
>9. sqoop   



## 目录文件简介
* **kingnetdc 目录为安装包版本的代码目录,进行了功能划分的设计**

> setup.py 打包工具文件
>> * 打包: python3.6 setup.py sdist
>> 在当前目录下生成 dist,kingnet.egg-info目录,安装文件在dist目录下,名称是kingnet-2.0.0.tar.gz  
>> * 查询: pip3.6 search -i http://pypi.op.kingnet.com "kingnetdc" --trusted-host pypi.op.kingnet.com
>> * 安装: sudo pip3.6 install -i http://pypi.op.kingnet.com "kingnetdc>x.x.x" --trusted-host pypi.op.kingnet.com
  需要制定版本,否则本地版本不会被覆盖安装
>> * 测试安装成功: python3.6 -c "import kingnetdc; print(kingnetdc.kdc.workDate))" 输出日期

> kingnet 目录为源码
>> * \_\_init\_\_.py 定义api
>> * constant.py 全局常量定义(配置表)
>> * db_utils.py 数据库操作函数
>> * msg_utils.py 消息发送函数(微信及电话)
>> * time_utils.py 日期时间操作函数
>> * kdc.py 机器负载检查,hql提交等功能
>> * utils.py 常用linux命令函数
>> * dana_data.py 数据上报模块v2.0版本,提供api
>> * config_utils.py 检查及获取配置文件的模块
>> * redis_utils.py redis单点及集群操作模块

> config 配置文件目录 
>> * \_\_init\_\_.py 声明这个目录是包目录
>> * kingnetdc.conf 必要配置文件,内部已经配置的内容为必须的配置项不可缺省




