## Description

Backend for food delivery application.

## Dependencies

You must install the following dependencies so you can build the project.

- JDK 11
- Git
- Docker

## How to run

#### Using Docker

 ```
 git clone https://github.com/cc-intern/voltaire cd voltaire
 cd voltaire
 docker build -t voltaire:latest .
 docker run -p 8080:8080 -d voltaire
 ```

#### Using maven

```
git clone https://github.com/cc-intern/voltaire
cd voltaire
./mvnw spring-boot:run
```

## CI/CD

The project is configured to automatically build and push container image to Google Cloud Platform Container Registry, and deploy the image to Cloud Run using GitHub Actions.

## Testing

To launch tests run `./mvnw clean test`

## REST API

[REST API documentation]

[REST API documentation]: https://documenter.getpostman.com/view/8774628/TVRecVeS
