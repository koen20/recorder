FROM alpine AS builder

# Download QEMU, see https://github.com/docker/hub-feedback/issues/1261
ENV QEMU_URL https://github.com/balena-io/qemu/releases/download/v5.2.0%2Bbalena4/qemu-5.2.0.balena4-arm.tar.gz
RUN apk add curl && curl -L ${QEMU_URL} | tar zxvf - -C . --strip-components 1

#debian:stable-slim
FROM debian@sha256:1ec158078b7216bd88034ec46c5fb55ac164143ae517e1a72de9deabbd5f40be AS builderApp

COPY --from=builder qemu-arm-static /usr/bin

RUN mkdir -p /usr/share/man/man1 && apt-get update && apt-get install -y openjdk-15-jdk tar
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN chmod +x gradlew
RUN ./gradlew clean build
RUN tar -xvf build/distributions/recorder-1.0.tar && rm -R build

FROM arm32v7/adoptopenjdk@sha256:5e402bdceb6ff79a07c131137d646d2924ff9d116a40902172e89cf7c41d192c
COPY --from=builder qemu-arm-static /usr/bin
ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builderApp /usr/src/app/recorder-1.0/* /app/
WORKDIR /app

CMD bin/recorder
