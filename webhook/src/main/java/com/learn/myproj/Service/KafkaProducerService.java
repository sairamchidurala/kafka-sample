package com.learn.myproj.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.myproj.DTO.WebhookData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "test-topic";

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(WebhookData webhookData) {
        try {
            String jsonData = objectMapper.writeValueAsString(webhookData);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, jsonData);

            future.thenAccept(result -> {
                System.out.println("✅ Message sent successfully! " +
                        "Topic: " + result.getRecordMetadata().topic() +
                        ", Partition: " + result.getRecordMetadata().partition() +
                        ", Offset: " + result.getRecordMetadata().offset());
            }).exceptionally(ex -> {
                System.err.println("❌ Message sending failed: " + ex.getMessage());
                return null; // Returning null to satisfy the lambda signature
            });
        } catch (Exception e) {
            System.err.println("❌ Error serializing message: " + e.getMessage());
        }
    }
}