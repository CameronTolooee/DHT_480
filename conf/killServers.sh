#!/bin/bash

if [ -z "$DHT_HOMEDIR" ]; then
	echo "Env variable \$DHT_HOMEDIR is not set"
	exit -1
fi

count=0
echo "Killing servers..."
while read line
do
	if [ $count == "0" ]; then
		pid=`ssh -n $line ps aux | grep "MeatDataServer" | awk '{print $2}'` # CHANGE TO META
		ssh -n $line kill $pid
	else
		pid=`ssh -n $line ps aux | grep "Server" | awk '{print $2}'`
		ssh -n $line kill $pid
	fi
	(( count++ ))
	echo "$line killed"
done < "servers"
