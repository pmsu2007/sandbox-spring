package com.mingxoop.sandbox.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class CursorUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String encode(Map<String, Object> cursorFields) {
        try {
            String json = objectMapper.writeValueAsString(cursorFields);
            return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode cursor", e);
        }
    }

    public static Map<String, Object> decode(String encodedCursor) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedCursor);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor format", e);
        }
    }

    public static Long getAsLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

}
