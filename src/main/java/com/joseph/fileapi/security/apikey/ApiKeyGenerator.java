package com.joseph.fileapi.security.apikey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ApiKeyGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
   
    public final record KeyHashPair(String apiKey, byte[] apiKeyHash) {
        public KeyHashPair {
            apiKeyHash = Arrays.copyOf(apiKeyHash, apiKeyHash.length);
        }

        @Override
        public byte[] apiKeyHash() { return Arrays.copyOf(apiKeyHash, apiKeyHash.length); }
    }

    public KeyHashPair generateApiKey(UUID apiKeyId) {
        if (apiKeyId == null) {
            throw new IllegalArgumentException("ApiKeyId cannot be null");
        }

        StringBuilder apiKeyBuilder = new StringBuilder();
        byte[] randomSecret = new byte[32];
        byte[] apiKeyHash;

        RANDOM.nextBytes(randomSecret);        
        
        apiKeyBuilder.append("fa_")
                    .append(apiKeyId)
                    .append(".")
                    .append(Base64.getUrlEncoder().withoutPadding().encodeToString(randomSecret));
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            apiKeyHash = digest.digest(apiKeyBuilder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return new KeyHashPair(apiKeyBuilder.toString(), apiKeyHash);
    }
}
