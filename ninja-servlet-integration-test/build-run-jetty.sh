#!/bin/sh

mvn package -DskipTests=true
docker build -f Dockerfile.jetty --tag=ninja-jetty .
docker run -it -p 8080:8080 ninja-jetty
