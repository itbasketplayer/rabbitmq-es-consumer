#!/bin/sh
#Program:

############# 全局变量说明 #####################
#
# $PID_FILE  用于记录pid号的文件
# $PID       pid号
# $ORIG_DIR  运行脚本时候的文件夹
# $MY_ROOT   脚本文件夹
# $STATUS    java执行状态返回
#
############# 全局变量说明结束 #################

PID_FILE="pid"
ORIG_DIR=$(pwd)

MY_ROOT=$(cd "$(dirname "$0")"; pwd)
cd $MY_ROOT

sh pid_checker.sh $PID_FILE
check=$?

if [ $check -eq 1 ]; then
        exit 1
fi

java	-cp . \
	-Xmx512m -Xms512m \
	-Djava.ext.dirs=./lib \
    -Dlog4j.configuration="file:./log4j.properties" \
	-Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN \
    -DshellName=$0 \
	com.youboy.search.Main $@ 1>rabbit-es-consumer.log 2>error.log &

#记录继承号并写入到文件
PID=$!
echo $PID>$PID_FILE

#跳回运行脚本时候的目录
cd ${ORIG_DIR}
status=$?
echo "-----------return ${status}--------"
exit ${status}

