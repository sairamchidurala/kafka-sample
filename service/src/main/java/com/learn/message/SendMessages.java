package com.learn.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.service.WebhookData;
import com.oracle.jrockit.jfr.ValueDefinition;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class SendMessages {
    public static void sendReplyToUser(String senderId, String messageType, WebhookData webhookData) throws Exception {
        if("text message".equals(messageType)) {
            messageType += String.format(" Message: %s", webhookData.getText());
        }
        if(webhookData.getPlatform().equals("messenger")) {
            Map<String, Object> messageData = Map.of(
                    "recipient", Map.of("id", senderId),
                    "message", Map.of("text", "You sent a " + messageType)
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(messageData);
            sendMessageToFb(payload, webhookData.getSourceId());
        } else if(webhookData.getPlatform().equals("whatsapp")) {
            String payload = String.format("""
                    {"messaging_product": "whatsapp","to":"%s","type":"text","text":{"body":"%s"}}
                    """, webhookData.getSenderId(), messageType);
            System.out.println(payload);
            SendWhatsappMessage(payload);
        }
    }

    public static String getAuthToken(String pageId) {
        @Value("${fb.page.tokens.419537554568805}")
        String fb_token;
        Map<String, String> pageTokens = Map.of(
                "419537554568805", fb_token
        );
        return pageTokens.getOrDefault(pageId, "DEFAULT_ACCESS_TOKEN");
    }

    public static void SendWhatsappMessage(String payload) throws Exception {
        String url = "https://waba-v2.360dialog.io/messages";
        @Value("${whatsapp.token}")
        String token;
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("D360-Api-Key", token) // Replace with your actual API key
                .defaultHeader("Content-Type", "application/json")
                .build();
//        payload = """
//                {"messaging_product":"whatsapp","recipient_type":"individual","to":"919985235601","type":"template","template":{"namespace":"c7e26b1f_7178_43c5_bbe1_6504e1d8f791","language":{"policy":"deterministic","code":"en"},"name":"itc_booklet","components":[{"type":"header","parameters":[{"image":{"link":"https://help.tataplay.com/uploads/templateimages/1736769463363.jpeg"},"type":"image"}]},{"type":"body","parameters":[]},{"type":"button","sub_type":"quick_reply","index":1,"parameters":[{"type":"payload","payload":"2"}]},{"type":"button","sub_type":"quick_reply","index":2,"parameters":[{"type":"payload","payload":"2"}]}]}}""";
        webClient.post()
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Message sent successfully: " + response))
                .doOnError(error -> System.err.println("Error sending message: " + error.getMessage())).subscribe();
    }

    public static void sendMessageToFb(String payload, String sourceId) throws Exception {
        String url = "https://graph.facebook.com/v18.0/me/messages?access_token=" + getAuthToken(sourceId);
        WebClient webClient = WebClient.create(url);

        // Send POST request using WebClient
        webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnTerminate(() -> System.out.println("Message sent successfully"))
                .doOnError(e -> System.err.println("Error sending message: " + e.getMessage()))
                .subscribe(); // Asynchronous call
    }
}
