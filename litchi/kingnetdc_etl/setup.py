#!/usr/bin/env python
#coding=utf8

from setuptools import setup, find_packages

setup(
    name="kingnetdc",
    version="1.0.4",
    packages=find_packages(),
    description="kingnetdc datacentre public lib",
    long_description=open('README.md').read(),
    author="sunyu",
    author_email="sunyu@kingnetdc.com",
    license="MIT License",
    url="https://gitlab.ops.kingnetdc.com/kdcetl/pytools.git",
    package_data={'': ['*.conf', '*.md']},
    install_requires=[
        'pyhive==0.5.2',
        'requests==2.18.4',
        'pymysql==0.8.0',
        'thrift_sasl==0.3.0',
        'redis-py-cluster==1.3.4',
        'redis==2.10.6',
        'pycurl==7.43.0.2',
        'python-dateutil==2.6.1',
    ]
)
