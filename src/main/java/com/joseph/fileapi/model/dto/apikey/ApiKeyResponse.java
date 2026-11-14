package com.joseph.fileapi.model.dto.apikey;

import java.time.OffsetDateTime;

import com.joseph.fileapi.model.constant.ApiKeyStatus;

public class ApiKeyResponse {
    private String apiKey;
    private ApiKeyStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    public ApiKeyResponse(String apiKey, ApiKeyStatus status, OffsetDateTime createdAt, OffsetDateTime expiresAt) {
        this.apiKey = apiKey;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getApiKey() { return this.apiKey; }
    public ApiKeyStatus getStatus() { return this.status; }
    public OffsetDateTime getCreatedAt() { return this.createdAt; }
    public OffsetDateTime getExpiresAt() { return this.expiresAt; }
}
