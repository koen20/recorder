FROM alpine AS builder

# Download QEMU, see https://github.com/docker/hub-feedback/issues/1261
ENV QEMU_URL https://github.com/balena-io/qemu/releases/download/v3.0.0%2Bresin/qemu-3.0.0+resin-arm.tar.gz
RUN apk add curl && curl -L ${QEMU_URL} | tar zxvf - -C . --strip-components 1

FROM debian@sha256:030ab272b197c7e534d4807c14842d751280fc8eec87aa00ae102abf19888e85

COPY --from=builder qemu-arm-static /usr/bin

RUN apt-get update && apt-get install -y software-properties-common && add-apt-repository "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main"
RUN mkdir -p /usr/share/man/man1 && apt-get update && apt-get install -y oracle-java8-installer tar
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN chmod +x gradlew
RUN ./gradlew clean build
RUN tar -xvf build/distributions/recorder-1.0.tar && rm -R build

ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

WORKDIR /usr/src/app/recorder-1.0

CMD bin/recorder
