#!/bin/sh
docker build -t javaoleg/micros-msg-broker:2024.0.0 .
docker image prune -f