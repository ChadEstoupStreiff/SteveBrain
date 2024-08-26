#!bin/bash
screen -d
cd -P $(dirname $0)
while true
	do echo "Starting ..."
	java -Xms2048M -Xmx4096M -jar ./spigot.jar nogui
	echo "Server stopped. Waiting for restart... (3 seconds)"
	sleep 3
done
