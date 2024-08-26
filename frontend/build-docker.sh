#!/bin/sh
docker build -t javaoleg/micros-frontend:2024.0.0 .
docker image prune -f