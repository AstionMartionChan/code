#!/bin/bash

cd `dirname $0`
source ~/.bash_profile

source ./kingnet.sh
db_info

table=$1
nextMonth=$2
sql="alter table $table drop partition p_other; alter table $table add partition("
for i in {0..35};do
  yearmonth=`date -d "$nextMonth-01 $i month" "+%Y%m"`
  #echo $yearmonth
  next=`expr $i + 1`
  #echo $next
  #day=`date -d "$next month" "+%Y-%m-01"`
  day=`date -d "$nextMonth-01 $next month" "+%Y-%m-01"`
  #echo $day
  sql="$sql partition p$yearmonth values less than ('$day'),"
done
sql="$sql partition p_other values less than maxvalue);"
echo $sql
echo "--------------------------------------------------------------------------------"
echo $sql | mysql -h $db_ip -u $db_user -p$db_pwd -P$db_port
