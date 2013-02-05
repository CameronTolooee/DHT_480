#!/bin/bash


count=0
echo "Killing servers..."
while read line
do
	if [ $count == "0" ]; then
		pid=`ssh -n $line ps aux | grep "java Server" | awk '{print $2}'` # CHANGE TO META
		ssh -n $line kill $pid
	else
		pid=`ssh -n $line ps aux | grep "java Server" | awk '{print $2}'`
		ssh -n $line kill $pid
	fi
	(( count++ ))
	echo "$line killed"
done < "servers"
