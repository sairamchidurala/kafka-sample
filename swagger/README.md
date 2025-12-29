# API Documentation

This directory contains the OpenAPI specification and documentation for the Meta Webhook Processor API.

## Files

- `openapi.yaml` - OpenAPI 3.0.3 specification
- `index.html` - Swagger UI documentation viewer

## Usage

### View Documentation Locally

1. Serve the files using a local web server:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js
   npx serve .
   ```

2. Open `http://localhost:8000` in your browser

### API Endpoints

The API supports webhooks for:

- **Messenger**: `POST /webhook/messenger`
- **Instagram**: `POST /webhook/instadm` 
- **WhatsApp**: `POST /webhook/whatsapp`
- **Telegram**: `POST /webhook/telegram/{botToken}`

### Production URLs

- Webhook Server: https://webhook.iamsairam.in
- Service Server: https://service.iamsairam.in

## Testing

You can test the API endpoints using the Swagger UI interface or tools like curl, Postman, or Insomnia.