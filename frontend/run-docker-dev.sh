#!/bin/sh
docker container run --network dev-network -e SPRING_PROFILE=dev -v ./main/:/usr/share/nginx/html --detach --rm --publish 80:80 --name micros-frontend javaoleg/micros-frontend:2024.0.0