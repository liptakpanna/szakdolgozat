#!/bin/bash

cd frontend
npm set audit false
npm install --loglevel=error
npm run build --loglevel=error

BASE=3000
INCREMENT=10

port=$BASE

while [[ -n $(lsof -i tcp:$port) ]]; do
  port=$[port+INCREMENT]
done


serve -s build -l $port 
