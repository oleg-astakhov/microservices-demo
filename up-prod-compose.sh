#!/bin/sh
docker compose --file compose-common.yml  --file compose-prod.yml --project-name oleg-ast-micros up --detach