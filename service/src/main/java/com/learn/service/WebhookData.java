package com.learn.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class WebhookData {
    private String platform; // "messenger" or "instagram"
    private String senderId;
    private String sourceId; // Page ID (Messenger) or Business ID (Instagram)
    private String messageType;
    private String text;
    private String stickerId;
    private List<Attachment> attachments;

    @JsonProperty("object")
    private String object;

    @JsonProperty("entry")
    private List<Entry> entry;

    public WebhookData() {}

    // Constructor to parse Meta webhook data
    public WebhookData(String platform, String jsonString) {
        this.platform = platform;

        // Parse JSON to this object
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            WebhookData parsedData = objectMapper.readValue(jsonString, WebhookData.class);
            this.object = parsedData.getObject();
            this.entry = parsedData.getEntry();

            // Extract data from the entry
            if (entry != null && !entry.isEmpty()) {
                Entry firstEntry = entry.get(0);
                Messaging messaging = firstEntry.getMessaging().get(0);

                this.senderId = messaging.getSender().getId();
                this.sourceId = messaging.getRecipient().getId();

                // Extract message details
                Message message = messaging.getMessage();
                if (message != null) {
                    this.text = message.getText();
                    this.stickerId = message.getStickerId();
                    this.attachments = message.getAttachments();
                    this.messageType = determineMessageType();
                }
            }
        } catch (Exception e) {
            // Handle error in parsing
            e.printStackTrace();
        }
    }

    private String determineMessageType() {
        if (text != null) {
            return "text";
        } else if (stickerId != null) {
            return "sticker";
        } else if (attachments != null && !attachments.isEmpty()) {
            return "media - " + attachments.get(0).getType(); // Example: "media - image"
        }
        return "unknown";
    }

    // Getters and Setters
    public String getPlatform() { return platform; }
    public String getSenderId() { return senderId; }
    public String getSourceId() { return sourceId; }
    public String getMessageType() { return messageType; }
    public String getText() { return text; }
    public String getStickerId() { return stickerId; }
    public List<Attachment> getAttachments() { return attachments; }
    public String getObject() { return object; }
    public List<Entry> getEntry() { return entry; }

    // Inner classes (Attachment, Message, Messaging, Entry, etc.)
    public static class Attachment {
        private String type; // Example: "image", "video"
        private Payload payload;

        public String getType() { return type; }
        public Payload getPayload() { return payload; }

        public static class Payload {
            private String url;  // Media URL
            public String getUrl() { return url; }
        }
    }

    public static class Message {
        private String mid;
        private String text;
        private String stickerId;
        private List<Attachment> attachments;

        public String getMid() { return mid; }
        public String getText() { return text; }
        public String getStickerId() { return stickerId; }
        public List<Attachment> getAttachments() { return attachments; }
    }

    public static class Messaging {
        private Sender sender;
        private Recipient recipient;
        private long timestamp;
        private Message message;

        public Sender getSender() { return sender; }
        public Recipient getRecipient() { return recipient; }
        public long getTimestamp() { return timestamp; }
        public Message getMessage() { return message; }
    }

    public static class Sender {
        private String id;
        public String getId() { return id; }
    }

    public static class Recipient {
        private String id;
        public String getId() { return id; }
    }

    public static class Entry {
        private String id;
        private long time;
        private List<Messaging> messaging;

        public String getId() { return id; }
        public long getTime() { return time; }
        public List<Messaging> getMessaging() { return messaging; }
    }
}
