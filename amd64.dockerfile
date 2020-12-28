FROM openjdk:11 as builder

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN ./gradlew clean build
RUN tar -xvf build/distributions/recorder-1.0.tar

FROM openjdk:11-jre

ENV APPLICATION_USER ktor
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builder /usr/src/app/recorder-1.0/* /app/
WORKDIR /app

CMD bin/recorder
