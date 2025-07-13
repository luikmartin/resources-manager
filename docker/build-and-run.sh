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
sleep 10

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker/docker-compose.yml ps

echo "✅ Application is running!"
echo "🌐 API available at: http://localhost:8080"
echo "🗄️ Database available at: localhost:5432"

echo ""
echo "📋 Useful commands:"
echo "  - View logs: docker-compose -f docker/docker-compose.yml logs -f resources-manager"
echo "  - Stop services: docker-compose -f docker/docker-compose.yml down"
echo "  - View API docs: http://localhost:8080/swagger-ui.html"
echo "  - Health check: http://localhost:8080/actuator/health" 