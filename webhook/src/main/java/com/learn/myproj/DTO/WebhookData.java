package com.learn.myproj.DTO;

import java.time.Instant;

public class WebhookData {
    private String id;
    private String payload; // Ensure this field exists

    public WebhookData(WebhookRequest request) {
        id = request.getId();
        payload = request.getPayload();
    }


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "WebhookData{id='" + id + "', payload='" + payload + "}";
    }
}
