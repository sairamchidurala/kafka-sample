package com.learn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceApplicationTests {

	// @Test
	void contextLoads() throws Exception {
		String data = """
				{"source":"whatsapp","payload":{"object":"whatsapp_business_account","entry":[{"id":"992057311587620","changes":[{"value":{"messaging_product":"whatsapp","metadata":{"display_phone_number":"12143969525","phone_number_id":"547643665095640"},"contacts":[{"profile":{"name":"hellokiller63"},"wa_id":"919985235601"}],"messages":[{"from":"919985235601","id":"wamid.HBgMOTE5OTg1MjM1NjAxFQIAEhgUM0EwQ0U4NjhEOUZBQjNBODRDMzIA","timestamp":"1741242016","text":{"body":"abcdefg"},"type":"text"}]},"field":"messages"}]}]}}
				""";
		// String data = """
		// {"source":"messenger","payload":{"object":"page","entry":[{"time":1740984381602,"id":"419537554568805","messaging":[{"sender":{"id":"8300520303390501"},"recipient":{"id":"419537554568805"},"timestamp":1740984380942,"message":{"mid":"m_rv2AVR_5hthZrGawaa80kKXpvyoW4ey9lzg3I7ClM24XbY_hDkXgGQqtc33claVf03ABCYfQAL8FVDN4vyY9jg","attachments":[{"type":"image","payload":{"url":"https://scontent.xx.fbcdn.net/v/t1.15752-9/480937815_949200667372913_3748486160290402352_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=fc17b8&_nc_ohc=ek78920ojNEQ7kNvgEKjZMR&_nc_oc=AdhoIDu0h4kI9L1Y78f3U8-dx_NoVtu7J9r2CLhHAW-S_MIDe81mdlL7OIvJaj3FMcOl6Y9McOlySBvozLaQR7Tw&_nc_ad=z-m&_nc_cid=0&_nc_zt=23&_nc_ht=scontent.xx&oh=03_Q7cD1gFWdS6XSN_tA6QJdryqVSABHNSCF6zDXlTca914bvXv_w&oe=67ECC107"}}]}}]}]}}
		// """;

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode rootNode = objectMapper.readTree(data);

		String source = rootNode.get("source").asText();
		JsonNode payload = rootNode.get("payload");
		System.out.println("source: " + source + ", Payload: " + payload.toString());

		WebhookData parsedData = objectMapper.readValue(payload.toString(), WebhookData.class);
	}

}
