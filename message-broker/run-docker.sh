#!/bin/sh
docker container run --network dev-network -v dev-micros-rabbitmq-vol:/var/lib/rabbitmq --hostname msg-broker-rabbit --detach --rm --publish 15672:15672 --publish 5672:5672 --publish 15692:15692 --name micros-msg-broker javaoleg/micros-msg-broker:2024.0.0

# Note about --hostname msg-broker-rabbit:
# RabbitMQ is that it stores data based on what it calls the "Node Name",
# which defaults to the hostname. What this means for usage in Docker
# is that we should specify -h/--hostname explicitly for each daemon so
# that we don't get a random hostname and can keep track of our data.