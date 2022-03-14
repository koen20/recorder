FROM eclipse-temurin:17 as builder

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN chmod +x gradlew
RUN ./gradlew clean build
RUN tar -xvf build/distributions/recorder-1.0.tar

FROM eclipse-temurin:17

ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builder /usr/src/app/recorder-1.0/ /app/
WORKDIR /config

CMD /app/bin/recorder
