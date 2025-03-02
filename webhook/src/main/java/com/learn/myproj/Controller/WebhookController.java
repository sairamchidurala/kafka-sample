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

    @PostMapping("/messenger")
    public String receiveMessengerWebhook(@RequestBody WebhookRequest request) {
        try {
            WebhookData webhookData = new WebhookData(request, "messenger"); // Set platform to "messenger"
            String jsonData = objectMapper.writeValueAsString(webhookData);
            System.out.println(jsonData);
            kafkaProducerService.sendMessage(webhookData);
            return "Messenger Webhook received and forwarded to Kafka!";
        } catch (JsonProcessingException e) {
            return "Error converting Messenger webhook data to JSON";
        }
    }

    @PostMapping("/instadm")
    public String receiveInstaDmWebhook(@RequestBody WebhookRequest request) {
        try {
            WebhookData webhookData = new WebhookData(request, "instagram"); // Set platform to "instagram"
            String jsonData = objectMapper.writeValueAsString(webhookData);
            System.out.println(jsonData);
            kafkaProducerService.sendMessage(webhookData);
            return "Instagram Webhook received and forwarded to Kafka!";
        } catch (JsonProcessingException e) {
            return "Error converting Instagram webhook data to JSON";
        }
    }
}
