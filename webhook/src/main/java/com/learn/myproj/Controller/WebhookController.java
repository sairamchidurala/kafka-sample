package com.learn.myproj.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.myproj.DTO.WebhookData;
import com.learn.myproj.Service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{source}")
    public String receiveMessengerWebhook(@PathVariable String source, @RequestBody WebhookData request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            WebhookData webhookData = new WebhookData(source, json);
            kafkaProducerService.sendMessage(objectMapper.writeValueAsString(webhookData));
            return String.format("%s Webhook received!", source);
        } catch (JsonProcessingException e) {
            return String.format("Error converting %s webhook data to JSON", source);
        }
    }
}
