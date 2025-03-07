package com.learn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.Configuration.KafkaConsumerConfig;
import com.learn.message.SendMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.consumer.topic}")
    public String topic;

    @Value("${spring.kafka.consumer.groupId}")
    public String groupId;

    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    public ConsumerService(KafkaConsumerConfig kafkaConsumerConfig) {
        this.kafkaConsumerConfig = kafkaConsumerConfig;
    }

    @KafkaListener(topics = "#{@kafkaConsumerConfig.topic}",
        groupId = "#{@kafkaConsumerConfig.groupId}")
    public void consume(String message) {
        try {
            logger.info("📥 Received Json: {}\ntopic: {}, groupId: {}", message, topic, groupId);
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