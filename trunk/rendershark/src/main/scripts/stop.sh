read LASTPID < "main.pid"
kill $LASTPID
rm -f main.pid