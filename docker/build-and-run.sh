#!/bin/bash

# Build and run the Resources Manager application with Docker Compose

set -e

echo "🐳 Building Resources Manager Application..."

# Navigate to the project root
cd "$(dirname "$0")/.."

# Build and start all services
echo "📦 Building application image..."
docker-compose -f docker/docker-compose.yml build resources-manager

echo "🚀 Starting all services..."
docker-compose -f docker/docker-compose.yml up -d

echo "⏳ Waiting for services to be ready..."
echo "📊 Waiting for Kafka to be healthy..."
timeout 120 bash -c 'until docker-compose -f docker/docker-compose.yml ps kafka | grep -q "healthy"; do sleep 5; done' || {
    echo "❌ Kafka failed to become healthy within 2 minutes"
    docker-compose -f docker/docker-compose.yml logs kafka
    exit 1
}

echo "🗄️ Waiting for PostgreSQL to be healthy..."
timeout 60 bash -c 'until docker-compose -f docker/docker-compose.yml ps db | grep -q "healthy"; do sleep 5; done' || {
    echo "❌ PostgreSQL failed to become healthy within 1 minute"
    docker-compose -f docker/docker-compose.yml logs db
    exit 1
}

echo "✅ All services are healthy!"

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker/docker-compose.yml ps

echo "✅ Application is running!"
echo "🌐 API available at: http://localhost:8061/resources-manager"
echo "🗄️ Database available at: localhost:5432"
echo "📊 Kafka UI available at: http://localhost:8062"

echo ""
echo "📋 Useful commands:"
echo "  - View logs: docker-compose -f docker/docker-compose.yml logs -f resources-manager"
echo "  - Stop services: docker-compose -f docker/docker-compose.yml down"
echo "  - View API docs: http://localhost:8061/resources-manager/swagger-ui.html"
echo "  - Health check: http://localhost:8061/resources-manager/actuator/health" 