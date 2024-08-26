#!/bin/sh
docker build -t javaoleg/micros-database:2024.0.0 .
docker image prune -f