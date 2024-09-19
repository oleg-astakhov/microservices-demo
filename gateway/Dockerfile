ARG BASE_IMAGE=azul/zulu-openjdk-alpine:22.0.1-22.30

FROM ${BASE_IMAGE} AS build-stage
WORKDIR /usr/local/docker-build
COPY gradle /usr/local/docker-build/gradle
COPY build.gradle settings.gradle gradle.properties gradlew /usr/local/docker-build/
RUN chmod +x gradlew
RUN ./gradlew build --refresh-dependencies || return 0

COPY src /usr/local/docker-build/src
RUN ./gradlew -i clean bootJar

FROM ${BASE_IMAGE}
ARG SERVICE_NAME=gateway
RUN mkdir -p /var/log/${SERVICE_NAME}
WORKDIR /usr/local/${SERVICE_NAME}
COPY --from=build-stage /usr/local/docker-build/build/libs/gateway.jar /usr/local/${SERVICE_NAME}/
COPY --from=javaoleg/micros-healthcheck:2024.0.0 /usr/local/healthcheck/healthcheck.jar /usr/local/${SERVICE_NAME}/
EXPOSE 8080
CMD ["java", "-Dreactor.schedulers.defaultBoundedElasticOnVirtualThreads=true", "-jar", "gateway.jar", "--spring.profiles.active=${SPRING_PROFILE}"]