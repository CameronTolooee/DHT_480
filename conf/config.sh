#!/bin/bash
# Cameron Tolooee

if [ -z "$DHT_HOMEDIR" ]; then
	echo "Env variable \$DHT_HOMEDIR is not set"
	exit -1
fi
if [ $# == 1 ]; then
	repLevel=$1
else
    repLevel=3
fi
final=$DHT_HOMEDIR
len=${#final} 
len=$((len--))
if [ ${final:$len} -e "/" ]; then
	len=$((len-1))
	final=${final:0:$len}
fi

count=0
echo "Starting servers..."
while read line
do
	echo $line
	ssh -nx $line mkdir -p /tmp/Tolooee_480 2> /dev/null
	if [ $count -eq "0" ]; then
		ssh -nx $line java -cp $final/bin/ MetaDataServer $final $repLevel & 2> /dev/null
	else
		ssh -nx $line java -cp $final/bin/ Server & 2> /dev/null
	fi
	((count++))
done < "$final/conf/servers"
