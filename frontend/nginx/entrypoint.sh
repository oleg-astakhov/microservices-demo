#!/bin/sh

if [ -z "$SPRING_PROFILE" ]; then
    echo "Error: SPRING_PROFILE is not set. Exiting..."
    exit 1
fi

if [ "$SPRING_PROFILE" = "dev" ]; then
    cp /usr/local/frontend/nginx-dev.conf /etc/nginx/conf.d/default.conf
elif [ "$SPRING_PROFILE" = "containerdev" ]; then
    cp /usr/local/frontend/nginx-containerdev.conf /etc/nginx/conf.d/default.conf
elif [ "$SPRING_PROFILE" = "composedev" ]; then
    cp /usr/local/frontend/nginx-prod.conf /etc/nginx/conf.d/default.conf
elif [ "$SPRING_PROFILE" = "compose-e2e-test" ]; then
    cp /usr/local/frontend/nginx-prod.conf /etc/nginx/conf.d/default.conf
elif [ "$SPRING_PROFILE" = "prod" ]; then
    cp /usr/local/frontend/nginx-prod.conf /etc/nginx/conf.d/default.conf
else
    echo "Error: Unhandled SPRING_PROFILE value [$SPRING_PROFILE]. Exiting..."
    exit 1
fi

nginx -g 'daemon off;'