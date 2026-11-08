package com.joseph.fileapi.model.entity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;

import com.joseph.fileapi.exception.exceptions.IllegalOperationException;
import com.joseph.fileapi.model.constant.ApiKeyStatus;
import com.joseph.fileapi.util.UUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "api_keys")
@DynamicInsert
public class ApiKey {
    @Id
    private UUID id;

    @Column (name = "key_hash", nullable = false)
    private byte[] keyHash;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "api_client_id", nullable = false)
    private ApiClient apiClient;

    @Column (name = "status", nullable = false)
    private ApiKeyStatus status;

    @Generated
    @Column (name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column (name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column (name = "revoked_at")
    private OffsetDateTime revokedAt;

    protected ApiKey() {}

    public ApiKey(byte[] keyHash, ApiClient apiClient, ApiKeyStatus status) {
        initialize(keyHash, apiClient, status);
    }

    public ApiKey(byte[] keyHash, ApiClient apiClient, ApiKeyStatus status, OffsetDateTime expiresAt) {
        this(keyHash, apiClient, status);
        this.expiresAt = expiresAt;
    }

    public void activate() {
        if (this.revokedAt != null) {
            throw new IllegalOperationException("Revoked keys cannot be activated");
        }
        this.status = ApiKeyStatus.ACTIVE;
    }
    public void deactivate() {
        this.status =  ApiKeyStatus.INACTIVE;
    }
    public void revoke() {
        this.deactivate();
        this.revokedAt = ((this.revokedAt != null) ? this.revokedAt : OffsetDateTime.now(ZoneOffset.UTC));
    }

    public UUID getId() { return this.id; }
    public byte[] getKeyHash() { return Arrays.copyOf(this.keyHash, this.keyHash.length); }
    public ApiClient getApiClient() { return this.apiClient; }
    public ApiKeyStatus getStatus() { return this.status; }
    public OffsetDateTime getCreatedAt() { return this.createdAt; }
    public OffsetDateTime getExpiresAt() { return this.expiresAt; }
    public OffsetDateTime getRevokedAt() { return this.revokedAt; }

    private void initialize(byte[] keyHash, ApiClient apiClient, ApiKeyStatus status) {
        if (keyHash == null) {
            throw new IllegalArgumentException("Keyhash cannot be null");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("ApiClient cannot be null");
        }

        this.id = UUIDGenerator.generateUUIDv7();
        this.keyHash = Arrays.copyOf(keyHash, keyHash.length);
        this.apiClient = apiClient;
        this.status = ((status == null) ? ApiKeyStatus.ACTIVE : status);
    }
}
