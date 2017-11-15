FROM openjdk:8-jdk-alpine

MAINTAINER jm5619

RUN mkdir /app

WORKDIR /app

ADD ./target/ir-property-catalogue-1.0.0-SNAPSHOT.jar /app

EXPOSE 8081

CMD ["java", "-jar", "ir-property-catalogue-1.0.0-SNAPSHOT.jar"]
