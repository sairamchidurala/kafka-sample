package com.learn.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelegramDTO {

    private Update update;

    // Constructor to parse the byte[] request into the Update object
    public TelegramDTO(byte[] request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Configure ObjectMapper to exclude null fields
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            this.update = objectMapper.readValue(request, Update.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Telegram webhook request", e);
        }
    }

    // Getter for the parsed Update object
    public Update getUpdate() {
        return update;
    }

    // Represents a Telegram user or bot
    public static class User {
        public long id;
        public boolean is_bot;
        public String first_name;
        public String last_name;
        public String username;
        public String language_code;
    }

    // Represents a chat (user, group, supergroup, or channel)
    public static class Chat {
        public long id;
        public String type; // "private", "group", "supergroup", or "channel"
        public String title;
        public String username;
        public String first_name;
        public String last_name;
    }

    // Represents a message entity (e.g., hashtags, mentions, URLs)
    public static class MessageEntity {
        public String type; // "mention", "hashtag", "cashtag", "bot_command", etc.
        public int offset;
        public int length;
        public String url;
        public User user;
        public String language;
    }

    // Represents a photo size (used in photo messages)
    public static class PhotoSize {
        public String file_id;
        public String file_unique_id;
        public int width;
        public int height;
        public int file_size;
    }

    // Represents an audio file
    public static class Audio {
        public String file_id;
        public String file_unique_id;
        public int duration;
        public String performer;
        public String title;
        public String mime_type;
        public int file_size;
        public PhotoSize thumb;
    }

    // Represents a document
    public static class Document {
        public String file_id;
        public String file_unique_id;
        public PhotoSize thumb;
        public String file_name;
        public String mime_type;
        public int file_size;
    }

    // Represents a video
    public static class Video {
        public String file_id;
        public String file_unique_id;
        public int width;
        public int height;
        public int duration;
        public PhotoSize thumb;
        public String mime_type;
        public int file_size;
    }

    // Represents a voice message
    public static class Voice {
        public String file_id;
        public String file_unique_id;
        public int duration;
        public String mime_type;
        public int file_size;
    }

    // Represents a contact
    public static class Contact {
        public String phone_number;
        public String first_name;
        public String last_name;
        public long user_id;
        public String vcard;
    }

    // Represents a location
    public static class Location {
        public float longitude;
        public float latitude;
        public float horizontal_accuracy;
        public int live_period;
        public int heading;
        public int proximity_alert_radius;
    }

    // Represents a poll option
    public static class PollOption {
        public String text;
        public int voter_count;
    }

    // Represents a poll
    public static class Poll {
        public String id;
        public String question;
        public PollOption[] options;
        public int total_voter_count;
        public boolean is_closed;
        public boolean is_anonymous;
        public String type; // "regular" or "quiz"
        public boolean allows_multiple_answers;
        public int correct_option_id;
    }

    // Represents a message
    public static class Message {
        public int message_id;
        public User from;
        public Chat chat;
        public int date;
        public String text;
        public MessageEntity[] entities;
        public PhotoSize[] photo;
        public Audio audio;
        public Document document;
        public Video video;
        public Voice voice;
        public Contact contact;
        public Location location;
        public Poll poll;
        public Message reply_to_message;
    }

    // Represents an incoming update from Telegram (webhook event)
    public static class Update {
        public int update_id;
        public Message message;
        public Message edited_message;
        public Message channel_post;
        public Message edited_channel_post;
        public Poll poll;
    }

    // Represents a webhook request payload
    public static class WebhookRequest {
        public Update update;
    }
}
