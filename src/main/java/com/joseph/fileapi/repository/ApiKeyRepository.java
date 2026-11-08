package com.joseph.fileapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joseph.fileapi.model.entity.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByIdAndApiClient_Id(UUID id, UUID apiClientId);
    List<ApiKey> findByApiClient_Id(UUID apiClientId);
}
