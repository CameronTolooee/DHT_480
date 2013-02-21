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
			pid=`ssh -n $line ps aux | grep "MetaDataServer" | awk '{print $2}'`
		for tokill in $pid; do
			if [ $tokill != "" ]; then 
				if [ $line == `hostname` ]; then
					kill $tokill 2> /dev/null
				else
					ssh -n $line kill $tokill 2> /dev/null  
				fi
			fi
		done
		echo "MetaServer: $line killed"
	else
		pid=`ssh -n $line ps aux | grep "Server" | awk '{print $2}'`
		for tokill in $pid; do
			if [ $tokill != "" ]; then 
				if [ $line == `hostname` ]; then
					kill -9 $tokill 2> /dev/null
				else
					ssh -n $line kill $tokill 2> /dev/null 
				fi
			fi
		done
	echo "Server: $line killed"
	fi
	(( count++ ))

done < "servers"
