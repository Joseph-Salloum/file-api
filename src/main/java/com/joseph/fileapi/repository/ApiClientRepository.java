package com.joseph.fileapi.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joseph.fileapi.model.entity.ApiClient;

@Repository 
public interface ApiClientRepository extends JpaRepository<ApiClient, UUID> {

}
