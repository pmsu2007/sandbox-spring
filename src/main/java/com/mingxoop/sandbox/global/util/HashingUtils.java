package com.mingxoop.sandbox.global.util;

import java.security.MessageDigest;
import java.util.Base64;

public class HashingUtils {

    public enum Encoding {
        BASE64
    }

    public static String hash(String input, String algorithm, Encoding encoding) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(input.getBytes());

            return switch (encoding) {
                case BASE64 -> Base64.getEncoder().encodeToString(hashedBytes);
            };

        } catch (Exception e) {
            throw new RuntimeException("Error hashing input: " + e.getMessage(), e);
        }
    }

    public static String sha256Base64(String input) {
        return hash(input, "SHA-256", Encoding.BASE64);
    }
}
