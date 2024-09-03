#!/bin/sh
docker compose --project-name micros-dev down
docker image prune -f