## Dependencies

You must install the following dependencies so you can build the project.

- JDK 11
- Git
- Docker

## How to run

#### Using Docker

1. `git clone https://github.com/cc-intern/voltaire`
2. `cd voltaire`
3. `docker build -t voltaire:latest .`
4. `docker run -p 8080:8080 -d voltaire`

#### Using maven

1. `git clone https://github.com/cc-intern/voltaire`
2. `cd voltaire`
3. `./mvnw spring-boot:run`

## CI/CD

The project is configured to automatically build and push container image to Google Cloud Platform Container Registry, and deploy the image to Cloud Run using GitHub Actions.

## Testing

To launch tests run `./mvnw clean test`

## REST API

[REST API documentation]

[REST API documentation]: https://documenter.getpostman.com/view/8774628/TVRecVeS
