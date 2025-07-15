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

## Quick Start

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

## Project Structure

- `api/` - Spring Boot application
- `common/` - Shared DTOs and enums
- `core/` - Domain entities and services
- `liquibase/` - Database migrations
- `docker/` - Container configuration
