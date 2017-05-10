#!/bin/bash

PID_FILE="pid"

ORIG_DIR=$(pwd)

MY_ROOT=$(cd "$(dirname "$0")"; pwd)
cd $MY_ROOT

kill -9 `cat $PID_FILE` && rm $PID_FILE -f
STATUS=$?

if [ $STATUS -eq 0 ];then
	echo success
else
	echo fail
fi
exit $STATUS
