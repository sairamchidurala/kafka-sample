package com.learn.myproj.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learn.myproj.Service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private static final String TOPIC = "webhook-topic";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping(value = {"/telegram/{botToken}", "/{source}"})
    public String receiveMessengerWebhook(
            @PathVariable(required = false) String source,
            @PathVariable(required = false) String botToken,
            @RequestBody String requestBody) {
        try {
            String sourceName = (source == null) ? "telegram/" + botToken : source;

            logger.info("Received data for {}: {}", sourceName, requestBody);
            String wrappedMessage = wrapMessageWithSource(sourceName, requestBody);
            logger.info("Wrapped message for {}: {}", sourceName, wrappedMessage);

            // Send the wrapped message to Kafka
            kafkaProducerService.sendMessage(wrappedMessage);

            return String.format("%s Webhook received!", sourceName);
        } catch (Exception e) {
            logger.error("Error processing webhook data for source: {}", source, e);
            return String.format("Error processing %s webhook data", source != null ? source : "telegram");
        }
    }

    // Helper method to wrap the message with the source
    private String wrapMessageWithSource(String source, String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode wrappedMessage = objectMapper.createObjectNode();
            wrappedMessage.put("source", source);
            wrappedMessage.set("payload", objectMapper.readTree(message));
            return objectMapper.writeValueAsString(wrappedMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to wrap message with source", e);
        }
    }
}
