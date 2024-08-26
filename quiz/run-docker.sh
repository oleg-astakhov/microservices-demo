#!/bin/sh
docker container run --network dev-network -e SPRING_PROFILE=containerdev --detach --rm --publish 8081:8080 --name micros-quiz javaoleg/micros-quiz:2024.0.0