package com.learn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.message.SendMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

@Service
public class ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        try {
            System.out.println("📥 Received Json: " + message);
            WebhookData webhookData = objectMapper.readValue(message, WebhookData.class);
            handleWebhook(webhookData);
        } catch (Exception e) {
            System.err.println("Error processing webhook data: " + e.getMessage());
        }
    }

    private void handleWebhook(WebhookData webhookData) {
        String platform = webhookData.getPlatform(); // "messenger" or "instagram"

        switch (platform) {
            case "messenger":
            case "whatsapp":
                try {
                    String senderId = webhookData.getSenderId();
                    String sourceId = webhookData.getSourceId();
                    String messageType = determineMessageType(webhookData);

                    SendMessages.sendReplyToUser(senderId, messageType, webhookData);
                } catch (Exception e) {
                    logger.error("Error processing webhook message: {}", e.getMessage());
                }
                break;
            default:
                logger.info("platform not handled: {}", platform);
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