#!/bin/sh

mvn clean package -DskipTests=true
docker build -f Dockerfile.tomcat --tag=ninja-tomcat2 .
docker run -it -p 8080:8080 ninja-tomcat2