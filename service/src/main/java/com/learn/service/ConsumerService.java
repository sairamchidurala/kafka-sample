package com.learn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerService.class);


//    public void listen(ConsumerRecord<String, String> record) {
//        System.out.println("📥 Received message: " + record.value());
//        System.out.println("Topic: " + record.topic() + ", Partition: " + record.partition() + ", Offset: " + record.offset());
//    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        try {
            WebhookData webhookData = objectMapper.readValue(message, WebhookData.class);
            System.out.println("📥 Received Json: " + message);
            handleWebhook(webhookData);
        } catch (Exception e) {
            System.err.println("Error processing webhook data: " + e.getMessage());
        }
    }

    private void handleWebhook(WebhookData webhookData) {
        String platform = webhookData.getPlatform(); // "messenger" or "instagram"
        String eventType = webhookData.getType();

        if ("messenger".equals(platform)) {
            handleMessengerEvent(eventType, webhookData);
        } else if ("instagram".equals(platform)) {
            handleInstagramEvent(eventType, webhookData);
        } else {
            System.out.println("Unhandled platform: " + platform);
        }
    }

    private void handleMessengerEvent(String eventType, WebhookData webhookData) {
        switch (eventType) {
            case "message":
                System.out.println("Messenger Message: " + webhookData.getMessage());
                break;
            default:
                System.out.println("Unhandled Messenger event: " + eventType);
        }
    }

    private void handleInstagramEvent(String eventType, WebhookData webhookData) {
        switch (eventType) {
            case "message":
                System.out.println("Instagram DM: " + webhookData.getMessage());
                break;
            default:
                System.out.println("Unhandled Instagram event: " + eventType);
        }
    }
}
