package com.learn.myproj.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.myproj.DTO.WebhookData;
import com.learn.myproj.DTO.WebhookRequest;
import com.learn.myproj.Service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final String TOPIC = "webhook-topic";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/receive")
    public String receiveWebhook(@RequestBody WebhookRequest request) {
        try {
            WebhookData webhookData = new WebhookData(request);
            String jsonData = objectMapper.writeValueAsString(webhookData); // Convert to JSON
            System.out.println(jsonData);
            kafkaProducerService.sendMessage(webhookData);
            return "Webhook received and forwarded to Kafka!";
        } catch (JsonProcessingException e) {
            return "Error converting webhook data to JSON";
        }
    }
}
