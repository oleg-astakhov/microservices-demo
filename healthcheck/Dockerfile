ARG BASE_IMAGE=azul/zulu-openjdk-alpine:22.0.1-22.30

FROM ${BASE_IMAGE} AS build-stage
WORKDIR /usr/local/docker-build
COPY gradle /usr/local/docker-build/gradle
COPY build.gradle settings.gradle gradle.properties gradlew /usr/local/docker-build/
RUN ./gradlew build --refresh-dependencies || return 0

COPY src /usr/local/docker-build/src
RUN ./gradlew clean shadowJar

FROM scratch
WORKDIR /usr/local/healthcheck
COPY --from=build-stage /usr/local/docker-build/build/libs/healthcheck.jar /usr/local/healthcheck/