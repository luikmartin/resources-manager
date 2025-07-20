# Resources Manager

Spring Boot application for managing resources (metering points and connection points) with multi-country support.

## Technologies

- **Backend**: Spring Boot 3.5.3, Java 21
- **Database**: PostgreSQL 17
- **ORM**: Spring Data JPA
- **Migrations**: Liquibase 4.27.0
- **Mapping**: MapStruct 1.5.5.Final (annotation-based object mapping)
- **Build**: Gradle with Spring Boot dependency management
- **Containerization**: Docker & Docker Compose
- **API Documentation**: SpringDoc OpenAPI 2.8.0 (Swagger)
- **Messaging**: Apache Kafka + Kafka UI (https://github.com/provectus/kafka-ui)
- **Testing**: JUnit 5, Testcontainers 1.20.3
- **Code Quality**: Lombok, JaCoCo (code coverage)
- **CI/CD**: GitHub Actions

## Continuous Integration (CI)

This project uses GitHub Actions for automated testing and quality assurance.

### CI Workflow

The CI pipeline automatically runs on:

- **Push** to `main` or `develop` branches
- **Pull requests** targeting `main` or `develop` branches

### CI Steps

The workflow performs the following steps in sequence:

1. **Build Project** - Compiles all code without running tests
2. **Unit Tests** - Executes all unit tests
3. **Integration Tests** - Runs integration tests with Testcontainers
4. **Artifact Upload** - Saves test results and coverage reports

### Viewing CI Results

- **GitHub Actions Tab**: Go to your repository → Actions tab to see workflow runs
- **Test Results**: Download the "test-results" artifact for detailed test output
- **Coverage Reports**: Download the "coverage-reports" artifact for JaCoCo coverage

### Manual CI Trigger

You can manually trigger the CI workflow:

1. Go to GitHub repository → Actions tab
2. Select "CI" workflow
3. Click "Run workflow" button
4. Choose branch and click "Run workflow"

## Prerequisites

- Java JDK 21
- Gradle (or use the included Gradle wrapper)
- Docker & Docker Compose (for PostgreSQL 17 and Apache Kafka)

## Local Development

### Setup Instructions

1. **Clone the repository:**

   ```bash
   git clone <repository-url>
   cd resources-manager
   ```

2. **Start external services (PostgreSQL and Kafka):**

   ```bash
   cd docker
   docker-compose up -d db kafka kafka-ui
   ```

3. **Verify services are running:**

   ```bash
   docker-compose ps
   ```

4. **Start the application:**

   ```bash
   ./gradlew :api:bootRun
   ```

5. **Access the API documentation:**
   - Open http://localhost:8061/resources-manager/swagger-ui.html
   - Explore available endpoints and test them directly from the browser

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
./gradlew integrationTest
```

**Start the application locally:**

```bash
./gradlew :api:bootRun
```

**Build without tests:**

```bash
./gradlew build -x integrationTest
```

## Docker Quick Start

**Start the complete application stack (including PostgreSQL, Kafka, and the Spring Boot app):**

```bash
cd docker
./build-and-run.sh
```

The application will log all available URLs when it starts up, making it easy to access the API and documentation.

## API Documentation

The application provides comprehensive API documentation using SpringDoc OpenAPI (Swagger).

### Available Documentation URLs

- **Swagger UI**: http://localhost:8061/resources-manager/swagger-ui.html

  - Interactive API documentation with built-in testing capabilities
  - Try out endpoints directly from the browser
  - View request/response schemas and examples

- **OpenAPI JSON**: http://localhost:8061/resources-manager/v3/api-docs

  - Machine-readable API specification in JSON format
  - Can be imported into tools like Postman or Insomnia

- **OpenAPI YAML**: http://localhost:8061/resources-manager/v3/api-docs.yaml
  - Alternative format for API specification
  - Useful for code generation tools

### API Endpoints Overview

The application provides RESTful APIs for managing:

- **Resources**: Create, read, update, and delete resources
- **Locations**: Manage location information
- **Characteristics**: Handle resource characteristics and metadata

## Access URLs

- **API Base URL**: http://localhost:8061/resources-manager
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

## Testing the API

### Quick API Tests

You can test the API using various methods:

#### 1. Using Swagger UI (Recommended for beginners)

1. Open http://localhost:8061/resources-manager/swagger-ui.html
2. Click on any endpoint to expand it
3. Click "Try it out" button
4. Fill in the required parameters
5. Click "Execute" to test the endpoint

#### 2. Using curl commands

**Get all resources:**

```bash
curl -X GET "http://localhost:8061/resources-manager/api/v1/resources" \
  -H "accept: application/json"
```

**Create a new resource:**

```bash
curl -X POST "http://localhost:8061/resources-manager/api/v1/resources" \
  -H "accept: application/json" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Resource",
    "type": "METERING_POINT",
    "countryCode": "EE"
  }'
```

**Get all locations:**

```bash
curl -X GET "http://localhost:8061/resources-manager/api/v1/locations" \
  -H "accept: application/json"
```

**Get all characteristics:**

```bash
curl -X GET "http://localhost:8061/resources-manager/api/v1/characteristics" \
  -H "accept: application/json"
```

#### 3. Using Postman or Insomnia

1. Import the OpenAPI specification from: http://localhost:8061/resources-manager/v3/api-docs
2. Use the generated collection to test all endpoints

### Testing Liquibase Functionality

#### 1. Verify Database Schema

Connect to the PostgreSQL database to verify that Liquibase migrations have been applied:

```bash
# Connect to PostgreSQL container
docker exec -it resources-manager-postgres-1 psql -U postgres -d resources_manager

# List all tables
\dt

# Check migration history
SELECT * FROM databasechangelog ORDER BY orderexecuted;
```

#### 2. Reset Database and Re-run Migrations

To test Liquibase migrations from scratch:

```bash
# Stop the application
./gradlew :api:bootRun --stop

# Drop and recreate the database
docker exec -it resources-manager-postgres-1 psql -U postgres -c "DROP DATABASE IF EXISTS resources_manager;"
docker exec -it resources-manager-postgres-1 psql -U postgres -c "CREATE DATABASE resources_manager;"

# Start the application (Liquibase will run automatically)
./gradlew :api:bootRun
```

#### 3. Check Migration Files

The Liquibase migration files are located in `liquibase/src/main/resources/db/liquibase/migrations/`:

### Integration Tests

Run the integration tests to verify the complete functionality:

```bash
# Run all integration tests
./gradlew integrationTest

# Run specific integration test
./gradlew :api:integrationTest --tests "*ResourceControllerIntegrationTest"
```

## Project Structure

- `api/` - Spring Boot application with REST controllers and integration tests
- `common/` - Shared DTOs, enums, and validation
- `core/` - Domain entities, services, and repositories
- `liquibase/` - Database migrations and schema management
- `docker/` - Container configuration and orchestration
