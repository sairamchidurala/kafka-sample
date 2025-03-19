package com.learn.Configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class Configs {
    private Map<String, Object> keysMap = new HashMap<>();

    private Object convertValue(String value) {
        if (value.matches("-?\\d+")) {
            return Integer.parseInt(value);
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(value);
        }
        return value;
    }

    public Object getKey(String id) {
        return keysMap.get("keys." + id);
    }

    public Map<String, Object> readApplicationProperties() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.properties");
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            properties.load(reader);
        }
        properties.forEach((key, value) -> {
            if(key.toString().startsWith("keys."))
                keysMap.put((String) key.toString().replace("keys.", ""), convertValue((String) value));
        });
        return keysMap;
    }
}
