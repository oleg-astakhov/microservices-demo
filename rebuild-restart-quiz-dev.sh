#!/bin/sh
docker compose --file compose-common.yml  --file compose-dev.yml --project-name micros-dev up --build --detach --no-deps quiz