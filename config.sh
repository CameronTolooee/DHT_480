#!/bin/bash
# Cameron Tolooee

count=0
echo "Starting servers..."
while read line
do
	echo $line
	ssh -nx $line mkdir -p /tmp/Tolooee_480
	if [ $count -eq "0" ]; then
		ssh -nx $line java Server &  # CHANGE THIS TO META
	else
		ssh -nx $line java Server &
	fi
	((count++))
done < "servers"
