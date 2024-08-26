#!/bin/sh
docker container run --network dev-network -e SPRING_PROFILE=containerdev --detach --rm --publish 8082:8080 --name gamification-dev javaoleg/micros-gamification:2024.0.0