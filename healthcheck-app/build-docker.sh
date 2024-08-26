#!/bin/sh
docker build -t javaoleg/micros-healthcheck:2024.0.0 .
docker image prune -f