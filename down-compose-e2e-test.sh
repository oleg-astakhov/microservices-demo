#!/bin/sh
docker compose --project-name micros-e2e-test down
docker image prune -f