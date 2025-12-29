#!/bin/bash

# Exit script immediately if any command fails
set -e

echo "🚀 Starting Deployment Process..."

# 1. Build Service
echo "🔨 Building Service JAR..."
cd service
mvn clean install -DskipTests
cd ..

# 2. Build Webhook
echo "🔨 Building Webhook JAR..."
cd webhook
mvn clean install -DskipTests
cd ..

# 3. Docker Compose
echo "🐳 Building and Starting Containers..."
docker compose -f docker-compose-apps.yml up -d --build

echo "✅ Deployment Complete! Current Status:"
docker ps | grep "service\|webhook"
