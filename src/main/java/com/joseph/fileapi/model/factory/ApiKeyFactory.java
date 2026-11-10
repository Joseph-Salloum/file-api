package com.joseph.fileapi.model.factory;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.joseph.fileapi.model.constant.ApiKeyStatus;
import com.joseph.fileapi.model.entity.ApiClient;
import com.joseph.fileapi.model.entity.ApiKey;
import com.joseph.fileapi.security.apikey.ApiKeyGenerator;
import com.joseph.fileapi.util.UUIDGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor 
public class ApiKeyFactory {
    private final ApiKeyGenerator apiKeyGenerator;

    public final record GenerationResult(String plaintextApiKey, ApiKey apiKey) {}

    public GenerationResult generate(ApiClient client, OffsetDateTime expiresAt) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        UUID keyId = UUIDGenerator.generateUUIDv7();
        ApiKeyGenerator.KeyHashPair pair = apiKeyGenerator.generateApiKey(keyId);

        ApiKey keyEntity = new ApiKey(keyId, pair.apiKeyHash(), client, ApiKeyStatus.ACTIVE, expiresAt);

        return new GenerationResult(pair.apiKey(), keyEntity);
    }
}
