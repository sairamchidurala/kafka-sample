package com.learn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.message.SendMessages;
import com.learn.message.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccessTokenService accessTokenService;

    @KafkaListener(topics = "#{@kafkaConsumerConfig.topic}",
        groupId = "#{@kafkaConsumerConfig.groupId}")
    public void consume(String message) {
        try {
            logger.info("📥 Received Json: {}", message);
            handleWebhook(message);
        } catch (Exception e) {
            logger.error("Error processing webhook data: {}", e.getMessage());
        }
    }

    private void handleWebhook(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode rootNode = objectMapper.readTree(message);

            String source = rootNode.get("source").asText();
            String sourceId = null;
            JsonNode payload = rootNode.get("payload");
            if(source != null) {
                String[] parts = source.split("/");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase("telegram")) {
                    source = parts[0];
                    sourceId = parts[1];
                }
            } else {
                throw new IllegalArgumentException("source not found");
            }

            switch (source) {
                case "messenger":
                case "whatsapp":
                    WebhookData webhookData = new WebhookData(source, payload.toString());
                    processWebhookData(webhookData);
                    break;
                case "telegram":
                    logger.info("\uD83D\uDCE8 Processing Telegram Webhook: {}", payload);
                    TelegramService telegramService = new TelegramService(accessTokenService);
                    telegramService.handlePayloadAndSendMessage(payload.toString(), sourceId);
                    break;
                default:
                    logger.warn("Platform not handled: {}", source);
            }
        } catch (Exception e) {
            logger.error("Error processing webhook message: {}", e.getMessage());
        }
    }

    private void processWebhookData(WebhookData webhookData) {
        try {
            String senderId = webhookData.getSenderId();
            String messageType = determineMessageType(webhookData);

            // Send reply to user
            new SendMessages(accessTokenService).sendReplyToUser(senderId, messageType, webhookData);
        } catch (Exception e) {
            logger.error("Error while sending message: {}", e.getMessage());
        }
    }

    private String determineMessageType(WebhookData webhookData) {
        if (webhookData.getText() != null) {
            return "text message";
        } else if (webhookData.getStickerId() != null) {
            return "sticker";
        } else if (webhookData.getAttachments() != null && !webhookData.getAttachments().isEmpty()) {
            return "media - " + webhookData.getAttachments().getFirst().getType(); // e.g., "media - image"
        }
        return "unknown";
    }
}