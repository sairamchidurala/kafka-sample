package com.learn.myproj.DTO;

public class WebhookRequest {
    private String id;
    private String payload;

    public WebhookRequest() {}

    public WebhookRequest(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "WebhookRequest{id='" + id + "', payload='" + payload + "'}";
    }
}
