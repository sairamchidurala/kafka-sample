package com.learn.myproj.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookData {
    private String platform; // "messenger" or "instagram"
    private String type; // "message", "comment", "reaction", etc.
    private String senderId;
    private String recipientId;
    private String message;
    private String timestamp;

    // Default constructor
    public WebhookData() {}

    // Constructor that maps incoming webhook request
    public WebhookData(WebhookRequest request, String platform) {
        this.platform = platform;

        if (platform.equals("messenger") && request.getEntry() != null && !request.getEntry().isEmpty()) {
            var messagingEvent = request.getEntry().getFirst().getMessaging().getFirst();
            this.senderId = messagingEvent.getSender().getId();
            this.recipientId = messagingEvent.getRecipient().getId();
            this.timestamp = String.valueOf(messagingEvent.getTimestamp());

            if (messagingEvent.getMessage() != null) {
                this.type = "message";
                this.message = messagingEvent.getMessage().getText();
            }
        } else if (platform.equals("instagram") && request.getEntry() != null && !request.getEntry().isEmpty()) {
            var messagingEvent = request.getEntry().getFirst().getMessaging().getFirst();
            this.senderId = messagingEvent.getSender().getId();
            this.recipientId = messagingEvent.getRecipient().getId();
            this.timestamp = String.valueOf(messagingEvent.getTimestamp());

            if (messagingEvent.getMessage() != null) {
                this.type = "message";
                this.message = messagingEvent.getMessage().getText();
            }
        }
    }

    // Getters and Setters
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}