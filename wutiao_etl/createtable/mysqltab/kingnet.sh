#!/bin/bash
function getMonthDay(){
   day=`date +%Y-%m-01` 
   dayTime=`date -d $day +%s`
   ((nextMonthTime=$dayTime+24 * 3600 * 32))
   nextMonth=`date -d "1970-1-1 UTC $nextMonthTime seconds" +%Y-%m`
   ((lastMonthTime=$nextMonthTime-24 * 3600 * 33))
   lastMonth=`date -d "1970-1-1 UTC $lastMonthTime seconds" +%Y-%m`
   nowMonth=`date +%Y%m`
   #echo $nextMonth
}

function db_info(){
    db_ip=172.27.0.255
    db_user=datac
    db_pwd=g3Z2zHF6uTxK6#rnlZ7
    db_port=3306
}

function db_info_test(){
    db_ip=172.27.2.251
    db_user=data_test
    db_pwd=data_test_pass
    db_port=4310
}

function db_info_91(){
    db_ip=172.17.2.91
    db_user=root
    db_pwd=123456
    db_port=3306
}