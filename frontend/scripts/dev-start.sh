#!/bin/bash

# Development Start Script
set -e

echo "🔧 Starting Development Environment..."

# Start backend
echo "🚀 Starting Spring Boot Backend..."
cd ../Fitness-Management-System
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ../frontend

# Wait for backend to start
sleep 10

# Start frontend
echo "🎨 Starting Angular Frontend..."
npm start &
FRONTEND_PID=$!

echo "✅ Development environment started!"
echo "🌐 Frontend: http://localhost:4200"
echo "🔧 Backend: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop all services..."

# Wait for interrupt
trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait
