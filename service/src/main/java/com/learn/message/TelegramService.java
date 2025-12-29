package com.learn.message;

import com.learn.DTO.TelegramDTO;
import com.learn.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

//    @Autowired
//    private AccessTokenService accessTokenService;

    private final AccessTokenService accessTokenService;

    @Autowired  // Optional in newer Spring versions if using constructor injection
    public TelegramService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

//    @Value("${telegram.bot.token}")
//    private static final String TELEGRAM_BOT_TOKEN = "7872018983:AAGV94nsgbceH_bEhu6Pic0LlvqQ6g3bhmI";
//    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage";

    private static String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage";
    private static String TELEGRAM_STICKER_URL = "https://api.telegram.org/bot%s/sendSticker";
    private static String TELEGRAM_MEDIA_URL = "https://api.telegram.org/bot%s/%s";
    private static String sourceId = "";

    /**
     * Handles the incoming payload and sends a response to Telegram.
     *
     * @param payload The payload received from the consumer (as a String).
     */
    public void handlePayloadAndSendMessage(String payload, String sourceId1) {
        try {
            sourceId = sourceId1;
            // Parse the payload using TelegramDTO
            TelegramDTO telegramDTO = new TelegramDTO(payload.getBytes());
            TelegramDTO.Update update = telegramDTO.getUpdate();

            // Extract chat ID and message content
            if (update != null && update.message != null) {
                long chatId = update.message.chat.id;

                // Handle text message
                if (update.message.text != null) {
                    String messageText = update.message.text;
                    // Prevent infinite loops by not echoing bot messages
                    if (!update.message.from.is_bot) {
                        sendMessageToTelegram(String.valueOf(chatId), messageText);
                    }
                }
                // Handle sticker message
                else if (update.message.sticker != null) {
                    String stickerFileId = update.message.sticker.file_id;
                    sendStickerToTelegram(String.valueOf(chatId), stickerFileId);
                }
                // Handle photo message
                else if (update.message.photo != null && update.message.photo.length > 0) {
                    String photoFileId = update.message.photo[0].file_id; // Use the first photo (highest resolution)
                    sendPhotoToTelegram(String.valueOf(chatId), photoFileId);
                }
                // Handle audio message
                else if (update.message.audio != null) {
                    String audioFileId = update.message.audio.file_id;
                    sendAudioToTelegram(String.valueOf(chatId), audioFileId);
                }
                // Handle document message
                else if (update.message.document != null) {
                    String documentFileId = update.message.document.file_id;
                    sendDocumentToTelegram(String.valueOf(chatId), documentFileId);
                }
                // Handle video message
                else if (update.message.video != null) {
                    String videoFileId = update.message.video.file_id;
                    sendVideoToTelegram(String.valueOf(chatId), videoFileId);
                }
                // Handle voice message
                else if (update.message.voice != null) {
                    String voiceFileId = update.message.voice.file_id;
                    sendVoiceToTelegram(String.valueOf(chatId), voiceFileId);
                }
                // Handle animation message
                else if (update.message.animation != null) {
                    String animationFileId = update.message.animation.file_id;
                    sendAnimationToTelegram(String.valueOf(chatId), animationFileId);
                }
                // Unsupported message type
                else {
                    System.err.println("Unsupported message type.");
                }
            } else {
                System.err.println("No valid message found in the payload.");
            }
        } catch (Exception e) {
            System.err.println("Error handling payload: " + e.getMessage());
        }
    }

    /**
     * Sends a sticker to a Telegram chat.
     *
     * @param chatId      The chat ID of the recipient.
     * @param stickerFileId The file ID of the sticker to send.
     */
    public void sendStickerToTelegram(String chatId, String stickerFileId) {
        try {
            // Prepare the request payload
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("chat_id", chatId);
            requestPayload.put("sticker", stickerFileId);

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HTTP entity
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Send the request to the Telegram API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(String.format(TELEGRAM_STICKER_URL, accessTokenService.getActiveAccessTokenBySourceId(sourceId)),
                    requestEntity,
                    String.class
            );

            // Log the response
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Sticker sent successfully: " + response.getBody());
            } else {
                System.err.println("Failed to send sticker: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error sending sticker to Telegram: " + e.getMessage());
        }
    }

    /**
     * Sends a message to a Telegram chat.
     *
     * @param chatId  The chat ID of the recipient.
     * @param message The message to send.
     */
    public void sendMessageToTelegram(String chatId, String message) {
        try {
            // Prepare the request payload
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("chat_id", chatId);
            requestPayload.put("text", message);

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HTTP entity
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Send the request to the Telegram API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(String.format(TELEGRAM_API_URL, accessTokenService.getActiveAccessTokenBySourceId(sourceId).toString()), requestEntity, String.class);

            // Log the response
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Message sent successfully: " + response.getBody());
            } else {
                System.err.println("Failed to send message: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error sending message to Telegram: " + e.getMessage());
        }
    }

    public void sendPhotoToTelegram(String chatId, String photoFileId) {
        sendMediaToTelegram(chatId, photoFileId, "sendPhoto", "photo");
    }

    public void sendAudioToTelegram(String chatId, String audioFileId) {
        sendMediaToTelegram(chatId, audioFileId, "sendAudio", "audio");
    }

    public void sendDocumentToTelegram(String chatId, String documentFileId) {
        sendMediaToTelegram(chatId, documentFileId, "sendDocument", "document");
    }

    public void sendVoiceToTelegram(String chatId, String voiceFileId) {
        sendMediaToTelegram(chatId, voiceFileId, "sendVoice", "voice");
    }

    public void sendVideoToTelegram(String chatId, String videoFileId) {
        sendMediaToTelegram(chatId, videoFileId, "sendVideo", "video");
    }

    public void sendAnimationToTelegram(String chatId, String animationFileId) {
        sendMediaToTelegram(chatId, animationFileId, "sendAnimation", "animation");
    }

    public void sendMediaToTelegram(String chatId, String fileId, String endpoint, String mediaType) {
        try {
            // Prepare the request payload
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("chat_id", chatId);
            requestPayload.put(mediaType, fileId);

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HTTP entity
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Send the request to the Telegram API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
//                    "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/" + endpoint,
                    String.format(TELEGRAM_MEDIA_URL, accessTokenService.getActiveAccessTokenBySourceId(sourceId).toString(), endpoint),
                    requestEntity,
                    String.class
            );

            // Log the response
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println(mediaType + " sent successfully: " + response.getBody());
            } else {
                System.err.println("Failed to send " + mediaType + ": " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error sending " + mediaType + " to Telegram: " + e.getMessage());
        }
    }
}