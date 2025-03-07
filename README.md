# Meta Webhook Processor using kafka

## Overview
This project is a Spring Boot application that processes webhooks from Meta (Facebook Messenger, Instagram, and WhatsApp). It receives webhook events, extracts relevant data, and forwards the structured message to a Kafka topic.

## Features
- Handles Messenger, Instagram, and WhatsApp webhooks
- Extracts sender ID, source ID, message type, and content
- Supports text, sticker, and media messages
- Sends structured messages to Kafka
- Replies to the user based on message type

## Tech Stack
- Java 17+
- Spring Boot 3.4.3
- Kafka
- Jackson (for JSON processing)
- RestTemplate (for sending replies)

## Configuration
### Kafka Setup
Ensure Kafka is running and update `application.yml`:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### Change Application Port
If port 8080 is in use, modify `application.yml`:
```yaml
server:
  port: 9090
```

## Endpoints
### 1. Messenger Webhook
```http
POST /webhook/messenger
```
**Sample Request:**
```json
{
  "object": "page",
  "entry": [{
    "id": "PAGE_ID",
    "messaging": [{
      "sender": { "id": "USER_ID" },
      "recipient": { "id": "PAGE_ID" },
      "message": { "text": "Hello" }
    }]
  }]
}
```

### 2. Instagram Webhook
```http
POST /webhook/instadm
```
**Sample Request:** (Similar structure to Messenger)

### 3. WhatsApp Webhook
```http
POST /webhook/whatsapp
```
**Sample Request:**
```json
{
  "object": "whatsapp_business_account",
  "entry": [{
    "id": "BUSINESS_ID",
    "changes": [{
      "value": {
        "messages": [{
          "from": "USER_PHONE",
          "id": "MESSAGE_ID",
          "text": { "body": "Hello" }
        }]
      }
    }]
  }]
}
```

### 4. Sending Replies
The service automatically detects the message type and replies using the Meta API.

## Running the Project
1. Clone the repository
2. Install dependencies:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Error Handling
- Logs errors with `logger.error()`
- Prints stack traces for debugging
