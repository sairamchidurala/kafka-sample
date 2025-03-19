package com.learn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.message.SendMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            JsonNode rootNode = objectMapper.readTree(message);

            String source = rootNode.get("source").asText();
            JsonNode payload = rootNode.get("payload");

            switch (source) {
                case "messenger":
                case "whatsapp":
                    WebhookData webhookData = new WebhookData(source, payload.toString());
                    processWebhookData(webhookData);
                    break;
                case "telegram":
                    logger.info("\uD83D\uDCE8 Processing Telegram Webhook: {}", payload);
                    // Add Telegram-specific processing logic here
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
            SendMessages.sendReplyToUser(senderId, messageType, webhookData);
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