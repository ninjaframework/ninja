#!/bin/sh

mvn -T 1C package -DskipTests=true
docker build -f Dockerfile.wildfly --tag=ninja-wildfly .
docker run -it -p 8080:8080 ninja-wildfly
