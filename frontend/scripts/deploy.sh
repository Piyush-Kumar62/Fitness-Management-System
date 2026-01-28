#!/bin/bash

# Deployment Script
set -e

echo "🚀 Deploying Fitness Management System..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | xargs)
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Build and start services
echo "🏗️  Building and starting services..."
docker-compose up -d --build

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check health
echo "🏥 Checking service health..."
docker-compose ps

# Show logs
echo "📋 Recent logs:"
docker-compose logs --tail=50

echo "✅ Deployment completed!"
echo "🌐 Frontend: http://localhost:80"
echo "🔧 Backend API: http://localhost:8080/api"
echo "📊 Swagger UI: http://localhost:8080/swagger-ui.html"
