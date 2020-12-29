FROM alpine AS builder

# Download QEMU, see https://github.com/docker/hub-feedback/issues/1261
ENV QEMU_URL https://github.com/balena-io/qemu/releases/download/v3.0.0%2Bresin/qemu-3.0.0+resin-arm.tar.gz
RUN apk add curl && curl -L ${QEMU_URL} | tar zxvf - -C . --strip-components 1

FROM bellsoft/liberica-openjdk-debian@sha256:ba760c31d6a730334aae61a82da94841d6327c3301a83af50c1370169634a4d2

COPY --from=builder qemu-arm-static /usr/bin

COPY ./build/distributions/recorder-1.0.tar /usr/src/app
WORKDIR /usr/src/app
RUN tar -xvf ./recorder-1.0.tar && rm recorder-1.0.tar

ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

WORKDIR /usr/src/app/recorder-1.0

CMD bin/recorder
