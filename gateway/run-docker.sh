#!/bin/sh
docker container run --network dev-network -e SPRING_PROFILE=containerdev --detach --rm --publish 8080:8080 --name micros-gateway javaoleg/micros-gateway:2024.0.0