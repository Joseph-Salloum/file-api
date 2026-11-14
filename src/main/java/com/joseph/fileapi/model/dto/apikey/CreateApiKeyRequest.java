package com.joseph.fileapi.model.dto.apikey;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreateApiKeyRequest {
    @NotNull 
    private UUID apiClientId;
    
    private OffsetDateTime expiresAt;

    public CreateApiKeyRequest() {}

    public UUID getApiClientId() { return this.apiClientId; }
    public OffsetDateTime getExpiresAt() { return this.expiresAt; }

    public void setApiClientId(UUID clientId) { this.apiClientId = clientId; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
