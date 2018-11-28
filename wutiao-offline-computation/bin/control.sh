#!/bin/bash

function start()
{
    echo $"Checking service state"
    # TODO: start it if it is dead

    rc=0
}

function stop()
{
    echo $"Checking service state"
    # TODO: stop it if it is running

    rc=0
}

function status()
{
    echo $"Current running process:"
    # TODO: add grep cmd
}

# See how we were called.
case "$1" in
start)
    start
    ;;
stop)
    stop
    ;;
status)
    status
    ;;
restart|reload|force-reload)
    stop
    start
    rc=$?
    ;;
*)
    echo $"Usage: $0 {start|stop|status|restart|reload|force-reload}"
    exit 2
esac

exit $rc
