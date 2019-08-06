FROM adoptopenjdk/openjdk11:latest

WORKDIR /app

COPY build/libs /app

EXPOSE 8080

CMD java -jar challenge-1.0.0-SNAPSHOT.jar