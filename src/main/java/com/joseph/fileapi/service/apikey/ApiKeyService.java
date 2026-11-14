package com.joseph.fileapi.service.apikey;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joseph.fileapi.exception.exceptions.ClientNotFoundException;
import com.joseph.fileapi.exception.exceptions.IllegalOperationException;
import com.joseph.fileapi.exception.exceptions.InvalidTimestampException;
import com.joseph.fileapi.model.dto.apikey.ApiKeyResponse;
import com.joseph.fileapi.model.dto.apikey.CreateApiKeyRequest;
import com.joseph.fileapi.model.entity.ApiClient;
import com.joseph.fileapi.model.entity.ApiKey;
import com.joseph.fileapi.model.factory.ApiKeyFactory;
import com.joseph.fileapi.repository.ApiClientRepository;
import com.joseph.fileapi.repository.ApiKeyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class ApiKeyService {
    private final ApiKeyFactory apiKeyFactory;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiClientRepository apiClientRepository;

    @Transactional 
    public ApiKeyResponse createApiKey(CreateApiKeyRequest request) {
        ApiClient client = apiClientRepository.findById(request.getApiClientId())
                .orElseThrow(() -> new ClientNotFoundException("No API client with the provided id"));
        
        if (!client.isEnabled()) {
            throw new IllegalOperationException("Cannot create an API key for a disabled API client");
        }
        if (request.getExpiresAt() != null && !validateExpirationTimestamp(request.getExpiresAt())) {
            throw new InvalidTimestampException("ExpiresAt value must be strictly later than current timestamp");
        }

        ApiKeyFactory.GenerationResult generationResult = apiKeyFactory.generate(client, request.getExpiresAt());

        return toResponse(generationResult.plaintextApiKey(), apiKeyRepository.save(generationResult.apiKey()));
    }

    private boolean validateExpirationTimestamp(OffsetDateTime expirationTimestamp) {
        return expirationTimestamp.isAfter(OffsetDateTime.now());
    }
    private ApiKeyResponse toResponse(String plaintextApiKey, ApiKey apiKey) {
        return new ApiKeyResponse(
                            plaintextApiKey,
                            apiKey.getStatus(),
                            apiKey.getCreatedAt(),
                            apiKey.getExpiresAt()
                    );
    }
}
