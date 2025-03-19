package com.learn.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebhookData {

    private static final Logger logger = LoggerFactory.getLogger(WebhookData.class);

    private String platform; // "messenger", "instagram", or "whatsapp"
    private String senderId;
    private String sourceId; // Page ID (Messenger), Business ID (Instagram), or WhatsApp phone number
    private String messageType;
    private String text;
    private String stickerId;
    private List<Attachment> attachments;

    @JsonProperty("object")
    private String object;

    @JsonProperty("entry")
    private List<Entry> entry;

    public WebhookData() {}

    // Constructor to parse webhook data, including Dialog360 payloads
    public WebhookData(String platform, String jsonString) {
        this.platform = platform;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            WebhookData parsedData = objectMapper.readValue(jsonString, WebhookData.class);
            this.object = parsedData.getObject();
            this.entry = parsedData.getEntry();

            if (entry != null && !entry.isEmpty()) {
                Entry firstEntry = entry.getFirst();

                if ("whatsapp".equals(platform)) {
                    // Handle Dialog360 WhatsApp payload
                    Change change = firstEntry.getChanges().getFirst();
                    Value value = change.getValue();
                    Dialog360Message waMessage = value.getMessages().getFirst();
                    this.senderId = waMessage.getFrom();
                    this.sourceId = value.getMetadata().getDisplayPhoneNumber();
                    this.text = waMessage.getText() != null ? waMessage.getText().getBody() : null;
                    this.messageType = waMessage.getType();
                } else {
                    // Handle Messenger/Instagram
                    Messaging messaging = firstEntry.getMessaging().getFirst();
                    this.senderId = messaging.getSender().getId();
                    this.sourceId = messaging.getRecipient().getId();
                    Message message = messaging.getMessage();
                    if (message != null) {
                        this.text = message.getText();
                        this.stickerId = message.getStickerId();
                        this.attachments = message.getAttachments();
                        this.messageType = determineMessageType();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing webhook data", e);
        }
    }

    private String determineMessageType() {
        if (text != null) {
            return "text";
        } else if (stickerId != null) {
            return "sticker";
        } else if (attachments != null && !attachments.isEmpty()) {
            return "media - " + attachments.getFirst().getType();
        }
        return "unknown";
    }

    // Getters and Setters...
    public String getObject() { return object; }
    public List<Entry> getEntry() { return entry; }
    public String getPlatform() { return platform; }
    public String getSenderId() { return senderId; }
    public String getSourceId() { return sourceId; }
    public String getMessageType() { return messageType; }
    public String getText() { return text; }
    public String getStickerId() { return stickerId; }
    public List<Attachment> getAttachments() { return attachments; }

    public static class Attachment {
        private String type;
        private Payload payload;

        public String getType() { return type; }
        public Payload getPayload() { return payload; }

        public static class Payload {
            private String url;
            public String getUrl() { return url; }
        }
    }

    public static class Message {
        private String mid; // Add this field
        private String text;
        private String stickerId;
        private List<Attachment> attachments;

        public String getMid() { return mid; } // Add getter
        public String getText() { return text; }
        public String getStickerId() { return stickerId; }
        public List<Attachment> getAttachments() { return attachments; }
    }

    public static class Messaging {
        private Sender sender;
        private Recipient recipient;
        private Message message;
        private long timestamp; // Add this field

        public Sender getSender() { return sender; }
        public Recipient getRecipient() { return recipient; }
        public Message getMessage() { return message; }
        public long getTimestamp() { return timestamp; } // Add getter
    }

    public static class Sender { private String id; public String getId() { return id; } }
    public static class Recipient { private String id; public String getId() { return id; } }

    public static class Entry {
        private String id; // Already added
        private List<Messaging> messaging;
        private List<Change> changes;
        private long time; // Add this field

        public String getId() { return id; }
        public List<Messaging> getMessaging() { return messaging; }
        public List<Change> getChanges() { return changes; }
        public long getTime() { return time; } // Add getter
    }


    public static class Contacts {
        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("wa_id")
        private String waId;

        public Profile getProfile() { return profile; }
        public String getWaId() { return waId; }
    }

    public static class Profile {
        private String name;
        public String getName() { return name; }
    }

    // Dialog360-specific classes
    public static class Change {
        private Value value;
        private String field; // Add this field

        public Value getValue() { return value; }
        public String getField() { return field; } // Add getter
    }

    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;

        private Metadata metadata;

        private List<Contacts> contacts;

        private List<Dialog360Message> messages;

        public String getMessagingProduct() { return messagingProduct; }
        public Metadata getMetadata() { return metadata; }
        public List<Contacts> getContacts() { return contacts; }
        public List<Dialog360Message> getMessages() { return messages; }
    }

    public static class Metadata {
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
        public String getPhoneNumberId() { return phoneNumberId; }

        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        public String getDisplayPhoneNumber() { return displayPhoneNumber; }
    }

    public static class Dialog360Message {
        private String id; // Already added
        private String from;
        private String type;
        private Dialog360Text text;
        private String timestamp; // Add this field

        public String getId() { return id; }
        public String getFrom() { return from; }
        public String getType() { return type; }
        public Dialog360Text getText() { return text; }
        public String getTimestamp() { return timestamp; } // Add getter
    }

    public static class Dialog360Text {
        private String body;
        public String getBody() { return body; }
    }
}