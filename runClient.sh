#!/bin/bash

cd frontend
npm run build

BASE=3000
INCREMENT=10

port=$BASE

while [[ -n $(lsof -i tcp:$port) ]]; do
  port=$[port+INCREMENT]
done


serve -s build -l $port
