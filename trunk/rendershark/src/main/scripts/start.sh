# rendershark startup script
#
# ernest.micklei@gmail.com

# start the engines
java \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.port= 9191 \
    -Dcom.sun.management.jmxremote.authenticate=false \
	-Dcom.sun.management.jmxremote.ssl=false \
    -classpath '../lib/*' \
    org.rendershark.http.HttpServer &

# store the java process id for stop.sh    
LASTPID=$(echo $!)
echo "${LASTPID}" > "main.pid"