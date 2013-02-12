#!/bin/bash
# Cameron Tolooee

if [ -z "$DHT_HOMEDIR" ]; then
	echo "Env variable \$DHT_HOMEDIR is not set"
	exit -1
fi
final=$DHT_HOMEDIR
len=${#final} 
len=$((len--))
if [ ${final:$len} -e "/" ]; then
	len=$((len-1))
	final=${final:0:$len}
	echo $len
fi

count=0
echo "Starting servers..."
while read line
do
	echo $line
	ssh -nx $line mkdir -p /tmp/Tolooee_480
	if [ $count -eq "0" ]; then
		ssh -nx $line java -cp $final/bin/ MetaDataServer $final 3 &
	else
		ssh -nx $line java -cp $final/bin/ Server &
	fi
	((count++))
done < "$final/conf/servers"
