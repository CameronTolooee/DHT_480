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
		echo killing metaNode
		pid=`ssh -n $line ps aux | grep "MeatDataServer" | awk '{print $2}'`
		for tokill in $pid; do
			if [ $tokill != "" ]; then 
				echo "ssh -n $line kill $tokill"
				ssh -n $line kill $tokill 
			fi
		done
	else
		pid=`ssh -n $line ps aux | grep "Server" | awk '{print $2}'`
		for tokill in $pid; do
			if [ $tokill != "" ]; then 
				echo "ssh -n $line kill $tokill"
				ssh -n $line kill $tokill 
			fi
		done
	fi
	(( count++ ))
	echo "$line killed"
done < "servers"
