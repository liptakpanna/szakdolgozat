#!/bin/bash

cd frontend

while getopts b opt; do
    case $opt in
        b) 
	npm install --loglevel=error
	npm run build --loglevel=error  ;;
        *) echo 'Error in parsing options' >&2
           exit 1
    esac
done

BASE=3000
INCREMENT=10

port=$BASE

while [[ -n $(lsof -i tcp:$port) ]]; do
  port=$[port+INCREMENT]
done

serve -s -n build -l $port 
