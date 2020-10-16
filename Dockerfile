FROM maven:3.6.0-jdk-11-slim AS builder
WORKDIR /app
COPY src ./src
COPY pom.xml .

RUN mvn -f pom.xml clean -Dmaven.test.skip=true package

FROM openjdk:11-jdk-slim
COPY --from=builder app/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]