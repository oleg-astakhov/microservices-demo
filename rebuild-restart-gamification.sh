#!/bin/sh
docker compose --file compose-common.yml  --file compose-dev.yml --project-name oleg-ast-micros up --build --detach --no-deps gamification