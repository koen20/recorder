FROM alpine AS builder

# Download QEMU, see https://github.com/docker/hub-feedback/issues/1261
ENV QEMU_URL https://github.com/balena-io/qemu/releases/download/v5.2.0%2Bbalena4/qemu-5.2.0.balena4-arm.tar.gz
RUN apk add curl && curl -L ${QEMU_URL} | tar zxvf - -C . --strip-components 1

#jre-11
FROM arm32v7/adoptopenjdk@sha256:5e402bdceb6ff79a07c131137d646d2924ff9d116a40902172e89cf7c41d192c

COPY --from=builder qemu-arm-static /usr/bin
ENV APPLICATION_USER ktor
RUN mkdir /usr/src/app && tar -xvf ./build/distributions/recorder-1.0.tar -C /usr/src/app && chown -R $APPLICATION_USER /usr/src/app && useradd -ms /bin/bash $APPLICATION_USER

WORKDIR /config
RUN chown -R $APPLICATION_USER /config
USER $APPLICATION_USER
CMD /usr/src/app/recorder-1.0/bin/recorder
