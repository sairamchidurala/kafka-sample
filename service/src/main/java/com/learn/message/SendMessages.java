package com.learn.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.service.AccessTokenService;
import com.learn.service.WebhookData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class SendMessages {

//    @Value("${fb.page.tokens.419537554568805}")
//    private static String fb_token;
//
//    @Value("${whatsapp.token}")
//    private static String token;

    private final AccessTokenService accessTokenService;

    @Autowired  // Optional in newer Spring versions if using constructor injection
    public SendMessages(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    public void sendReplyToUser(String senderId, String messageType, WebhookData webhookData) throws Exception {
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
            SendWhatsappMessage(payload, webhookData.getSourceId());
        }
    }

//    public static String getAuthToken(String pageId) {
//
//        Map<String, String> pageTokens = Map.of(
//                "419537554568805", fb_token
//        );
//        return pageTokens.getOrDefault(pageId, "DEFAULT_ACCESS_TOKEN");
//    }

    public void SendWhatsappMessage(String payload, String sourceId) throws Exception {
        String url = "https://waba-v2.360dialog.io/messages";
        String token = accessTokenService.getActiveAccessTokenBySourceId(sourceId);
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("D360-Api-Key", token) // Replace with your actual API key
                .defaultHeader("Content-Type", "application/json")
                .build();

        webClient.post()
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Message sent successfully: " + response))
                .doOnError(error -> System.err.println("Error sending message: " + error.getMessage())).subscribe();
    }

    public void sendMessageToFb(String payload, String sourceId) throws Exception {
        String token = accessTokenService.getActiveAccessTokenBySourceId(sourceId);
        String url = "https://graph.facebook.com/v18.0/me/messages?access_token=" + token;
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
