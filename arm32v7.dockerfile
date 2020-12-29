FROM alpine AS builder

# Download QEMU, see https://github.com/docker/hub-feedback/issues/1261
ENV QEMU_URL https://github.com/balena-io/qemu/releases/download/v3.0.0%2Bresin/qemu-3.0.0+resin-arm.tar.gz
RUN apk add curl && curl -L ${QEMU_URL} | tar zxvf - -C . --strip-components 1

FROM openjdk:8

COPY --from=builder qemu-arm-static /usr/bin

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN chmod +x gradlew
RUN ./gradlew clean build
RUN tar -xvf build/distributions/recorder-1.0.tar

ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

WORKDIR /usr/src/app/recorder-1.0

CMD bin/recorder
