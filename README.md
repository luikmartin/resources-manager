# Resources Manager

Spring Boot application for managing resources (metering points and connection points) with multi-country support.

## Technologies

- **Backend**: Spring Boot 3.x, Java 21
- **Database**: PostgreSQL 17
- **ORM**: Spring Data JPA
- **Migrations**: Liquibase
- **Mapping**: Manual Java mappers with builder pattern
- **Build**: Gradle
- **Containerization**: Docker & Docker Compose
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Messaging**: Apache Kafka + Kafka UI (https://github.com/provectus/kafka-ui)

## Prerequisites

- Java JDK 21
- Gradle (or use the included Gradle wrapper)
- Docker & Docker Compose (for PostgreSQL 17 and Apache Kafka)

## Local Development

### External Services Setup

The application requires PostgreSQL 17 and Apache Kafka, which are provided via Docker containers. You don't need to install these services locally.

**Start external services (PostgreSQL and Kafka):**

```bash
cd docker
docker-compose up -d postgres kafka kafka-ui
```

**Stop external services:**

```bash
cd docker
docker-compose down
```

### Gradle Commands

**Clean and build the project:**

```bash
./gradlew clean build
```

**Run tests:**

```bash
./gradlew test
```

**Start the application locally:**

```bash
./gradlew :api:bootRun
```

**Build without tests:**

```bash
./gradlew build -x test
```

**Run specific module tests:**

```bash
./gradlew :api:test
./gradlew :common:test
./gradlew :core:test
```

## Docker Quick Start

**Start the complete application stack (including PostgreSQL, Kafka, and the Spring Boot app):**

```bash
cd docker
./build-and-run.sh
```

The application will log all available URLs when it starts up, making it easy to access the API and documentation.

## Access URLs

- **API Base URL**: http://localhost:8061/resources-manager
- **Swagger UI**: http://localhost:8061/resources-manager/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8061/resources-manager/v3/api-docs
- **OpenAPI YAML**: http://localhost:8061/resources-manager/v3/api-docs.yaml
- **Health Check**: http://localhost:8061/resources-manager/actuator/health
- **Database**: localhost:5432 (PostgreSQL)
- **Kafka**: localhost:9092
- **Kafka UI**: http://localhost:8062

## Configuration Notes

> **Note**: The Kafka configuration in `docker/docker-compose.yml` includes two different `KAFKA_ADVERTISED_LISTENERS` settings:
>
> - `PLAINTEXT://kafka:9092` (currently active) - Use this when running the application in Docker
> - `PLAINTEXT://localhost:9092` (commented out) - Use this when running the application locally and connecting to Kafka in Docker
>
> Make sure to uncomment the appropriate setting based on your deployment scenario.

## Project Structure

- `api/` - Spring Boot application
- `common/` - Shared DTOs and enums
- `core/` - Domain entities and services
- `liquibase/` - Database migrations
- `docker/` - Container configuration
