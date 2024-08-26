#!/bin/sh
docker build -t javaoleg/micros-quiz:2024.0.0 .
docker image prune -f