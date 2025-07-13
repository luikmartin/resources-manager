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
- **Messaging**: Apache Kafka

## Quick Start

```bash
cd docker
./build-and-run.sh
```

## Access URLs

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Database**: localhost:5432 (PostgreSQL)
- **Kafka**: localhost:9092

## API Endpoints

- `GET/POST /api/resources` - Resource management (with Kafka notifications)
- `POST /api/resources/{id}/location` - Update location
- `POST/DELETE /api/resources/{id}/characteristics` - Manage characteristics
- `POST /api/resources/bulk-export` - Export all resources to Kafka

## Project Structure

- `api/` - Spring Boot application
- `common/` - Shared DTOs and enums
- `core/` - Domain entities and services
- `liquibase/` - Database migrations
- `docker/` - Container configuration
