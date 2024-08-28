# Initialization scripts

from https://hub.docker.com/_/postgres

## How the Script Gets Executed

After the image entrypoint calls `initdb` to create the default postgres user and database, it will run any `*.sql` files, run any executable `*.sh` scripts, and source any non-executable `*.sh` scripts found in directory `/docker-entrypoint-initdb.d` (creating the directory if necessary) to do further initialization before starting the service.

**Warning:** scripts in `/docker-entrypoint-initdb.d` are only run if you start the container with a data directory that is empty; any pre-existing database will be left untouched on container startup. One common problem is that if one of your `/docker-entrypoint-initdb.d` scripts fails (which will cause the entrypoint script to exit) and your orchestrator restarts the container with the already initialized data directory, it will not continue on with your scripts.
