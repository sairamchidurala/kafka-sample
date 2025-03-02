package com.learn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        if ("messenger".equals(platform)) {
            try {
                String senderId = webhookData.getSenderId();
                String sourceId = webhookData.getSourceId();
                String messageType = determineMessageType(webhookData);

                // Get authentication token for the sourceId (Page ID)
                String pageAccessToken = getAuthToken(sourceId);

                // Send reply
                sendReplyToUser(senderId, messageType, pageAccessToken);
            } catch (Exception e) {
                System.err.println("Error processing webhook message: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Unhandled platform: " + platform);
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

    private String getAuthToken(String pageId) {
        Map<String, String> pageTokens = Map.of(
                "419537554568805", "EAAD9bPA9SbMBOzzgs0QBglqa5tQaZCIUG70ZBRVKBXST2yaQwvpc6XqF6zL5QxvIFQHAh5Ioot1pDwfCFKkJ4M8PPHmIz3BWirhaFff1jaB9HZA8qpFdYBugwNnROCiosXBH7BoQ4sZA5CQdXofnIlgeLCtNfdtqG3NJ5bLC4JClfzNEhpL8TbHywpAZA1klMssWV5lBk"
        );
        return pageTokens.getOrDefault(pageId, "DEFAULT_ACCESS_TOKEN");
    }

    public void sendReplyToUser(String senderId, String messageType, String accessToken) {
        String url = "https://graph.facebook.com/v18.0/me/messages?access_token=" + accessToken;

        WebClient webClient = WebClient.create(url);

        // Construct the message payload
        Map<String, Object> messageData = Map.of(
                "recipient", Map.of("id", senderId),
                "message", Map.of("text", "You sent a " + messageType)
        );

        // Send POST request using WebClient
        webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(messageData)
                .retrieve()
                .bodyToMono(String.class)
                .doOnTerminate(() -> System.out.println("Message sent successfully"))
                .doOnError(e -> System.err.println("Error sending message: " + e.getMessage()))
                .subscribe(); // Asynchronous call
    }
}
