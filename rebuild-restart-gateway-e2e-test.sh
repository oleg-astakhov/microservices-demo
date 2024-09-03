#!/bin/sh
docker compose --file compose-common.yml  --file compose-e2e-test.yml --project-name micros-e2e-test up --build --detach --no-deps gateway