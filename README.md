DHT_480
Cameron Tolooee
Annika Muehlbradt
Feb 4th, 2013
**************************************************************

You must have password-less ssh on department machines. See www.cs.colostate.edu/~info/faq for more info. All servers currently run on port 35005.

Steps to get running:

1.) Compile with make

2.) edit the servers file to select which machines will included in the cluster. One machine name per line (ip addresses should work but untested). The first name listed will host the MetaDataServer.

3.) run ./killServers.sh to make sure there are no servers already up

4.) run ./config.sh to bring all servers in the config file up

5.) run java Client <file_name> (its currently hard coded to store only)

Work in progress!
