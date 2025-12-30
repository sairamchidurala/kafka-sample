#!/bin/bash

# Exit script immediately if any command fails
set -e

COMPOSE_FILE="docker-compose-apps.yml"

# Helper function to print usage
usage() {
    echo "Usage: $0 {start|stop|logs|restart}"
    echo "  start   : Build JARs and start Docker containers"
    echo "  stop    : Stop and remove containers"
    echo "  logs    : Follow container logs"
    echo "  restart : Stop, then Start"
    exit 1
}

# Function to build and start
start_app() {
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
    docker compose -f "$COMPOSE_FILE" up -d --build

    echo "✅ Deployment Complete! Current Status:"
    docker ps | grep "service\|webhook"
}

# Function to stop
stop_app() {
    echo "🛑 Stopping containers..."
    docker compose -f "$COMPOSE_FILE" down
    echo "✅ Containers stopped."
}

# Function to view logs
logs_app() {
    echo "tao logs..."
    docker compose -f "$COMPOSE_FILE" logs -f
}

# Main logic to handle arguments
case "$1" in
    start)
        start_app
        ;;
    stop)
        stop_app
        ;;
    logs)
        logs_app
        ;;
    restart)
        stop_app
        start_app
        ;;
    *)
        usage
        ;;
esac
