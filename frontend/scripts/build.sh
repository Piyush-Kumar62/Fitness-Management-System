#!/bin/bash

# Build All Services Script
set -e

echo "🏗️  Building Fitness Management System..."

# Build Backend
echo "📦 Building Spring Boot Backend..."
cd ../Fitness-Management-System
./mvnw clean package -DskipTests
cd ../frontend

# Build Frontend
echo "🎨 Building Angular Frontend..."
npm install
npm run build:prod

echo "✅ Build completed successfully!"
