package com.learn.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookRequest {

    private String object; // "page" for Messenger, "instagram" for Instagram

    private List<Entry> entry;

    // Getters and Setters
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    // Inner class to map the "entry" array
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private List<Messaging> messaging;

        public List<Messaging> getMessaging() {
            return messaging;
        }

        public void setMessaging(List<Messaging> messaging) {
            this.messaging = messaging;
        }
    }

    // Inner class to map "messaging" array
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Messaging {
        private Sender sender;
        private Recipient recipient;
        private long timestamp;
        private Message message;

        public Sender getSender() {
            return sender;
        }

        public void setSender(Sender sender) {
            this.sender = sender;
        }

        public Recipient getRecipient() {
            return recipient;
        }

        public void setRecipient(Recipient recipient) {
            this.recipient = recipient;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    // Inner class to map "sender" field
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    // Inner class to map "recipient" field
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recipient {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    // Inner class to map "message" field
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}