package com.joseph.fileapi.model.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;

import com.joseph.fileapi.util.UUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "api_clients")
@DynamicInsert 
public class ApiClient {
    @Id 
    private UUID id;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "is_enabled", nullable = false)
    private Boolean enabled;

    @Generated
    @Column (name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ApiClient() {}

    public ApiClient(String name, Boolean enabled) {
        this.id = UUIDGenerator.generateUUIDv7();
        changeName(name);
        this.enabled = ((enabled == null) ? true : enabled);
    }

    public void enable() { this.enabled = true; }
    public void disable() { this.enabled = false; }

    public UUID getId() { return this.id; }
    public String getName() { return this.name; }
    public Boolean isEnabled() { return this.enabled; }
    public OffsetDateTime getCreatedAt() { return this.createdAt; }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = newName;
    }
}
