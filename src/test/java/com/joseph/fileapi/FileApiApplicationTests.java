package com.joseph.fileapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.joseph.fileapi.exception.exceptions.IllegalOperationException;
import com.joseph.fileapi.model.constant.ApiKeyStatus;
import com.joseph.fileapi.model.entity.ApiClient;
import com.joseph.fileapi.model.entity.ApiKey;
import com.joseph.fileapi.repository.ApiClientRepository;
import com.joseph.fileapi.repository.ApiKeyRepository;
import com.joseph.fileapi.util.UUIDGenerator;

import jakarta.persistence.EntityManager;

@Transactional
@ActiveProfiles ("test")
@SpringBootTest
class FileApiApplicationTests {
	@Autowired 
	private ApiClientRepository apiClientRepository;

	@Autowired
	private ApiKeyRepository apiKeyRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void contextLoads() {
	}

	@Test
	void apiClientCreationTest() {
		String name = "test";
		Boolean enabled = null;
		ApiClient apiClient = new ApiClient(name, enabled);

		apiClientRepository.save(apiClient);

		entityManager.flush();
		entityManager.clear();

		ApiClient savedApiClient = apiClientRepository.findById(apiClient.getId()).orElse(null);

		assertNotNull(savedApiClient);
		assertEquals(name, savedApiClient.getName());
		assertEquals(true, savedApiClient.isEnabled());
		assertEquals(7, savedApiClient.getId().version());
		assertNotNull(savedApiClient.getCreatedAt());
	}

	@Test
	void apiClientChangeNameTest() {
		String name = "test";
		Boolean enabled = true;
		ApiClient apiClient = new ApiClient(name, enabled);

		assertThrows(IllegalArgumentException.class, () -> {
			apiClient.changeName(null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			apiClient.changeName("");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			apiClient.changeName("    ");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new ApiClient(null, enabled);
		});

		apiClient.changeName("new name");
		assertEquals("new name", apiClient.getName());
	}

	@Test
	void apiKeyCreationTest() {
		byte[] keyHash = new byte[] { 1, 2, 3 };
		ApiClient apiClient = new ApiClient("test", true);
		ApiKey apiKey = new ApiKey(keyHash, apiClient, null);

		assertNull(apiKey.getCreatedAt());

		apiClientRepository.save(apiClient);
		apiKeyRepository.save(apiKey);

		entityManager.flush();
		entityManager.clear();

		ApiKey savedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);

		assertNotNull(savedApiKey);
		for (int i = 0; i < keyHash.length; i++) {
			assertEquals(keyHash[i], savedApiKey.getKeyHash()[i]);
		}
		assertEquals(apiClient.getId(), savedApiKey.getApiClient().getId());
		assertEquals(ApiKeyStatus.ACTIVE, savedApiKey.getStatus());
		assertNotNull(savedApiKey.getCreatedAt());
		assertNull(savedApiKey.getExpiresAt());
		assertNull(savedApiKey.getRevokedAt());

		savedApiKey.deactivate();
		entityManager.flush();
		entityManager.clear();
		ApiKey deactivatedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);
		assertEquals(ApiKeyStatus.INACTIVE, deactivatedApiKey.getStatus());
	}

	@Test
	void apiKeyLifeCycleTest() {
		byte[] keyHash = new byte[] { 1, 2, 3 };
		ApiClient apiClient = new ApiClient("test", true);
		ApiKey apiKey = new ApiKey(keyHash, apiClient, ApiKeyStatus.ACTIVE);

		apiClientRepository.save(apiClient);
		apiKeyRepository.save(apiKey);

		entityManager.flush();
		entityManager.clear();

		ApiKey savedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);

		assertNotNull(savedApiKey);

		savedApiKey.deactivate();
		entityManager.flush();
		entityManager.clear();
		ApiKey deactivatedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);
		assertEquals(ApiKeyStatus.INACTIVE, deactivatedApiKey.getStatus());

		deactivatedApiKey.activate();
		entityManager.flush();
		entityManager.clear();
		ApiKey activatedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);
		assertEquals(ApiKeyStatus.ACTIVE, activatedApiKey.getStatus());
		
		activatedApiKey.revoke();
		entityManager.flush();
		entityManager.clear();
		ApiKey revokedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);
		assertEquals(ApiKeyStatus.INACTIVE, revokedApiKey.getStatus());
		assertNotNull(revokedApiKey.getRevokedAt());

		OffsetDateTime revokedAt = revokedApiKey.getRevokedAt();

		for (int i = 0; i < 10; i++) {
			revokedApiKey.revoke();
			assertEquals(revokedAt, revokedApiKey.getRevokedAt());
		}

		entityManager.flush();
		entityManager.clear();
		ApiKey reRevokedApiKey = apiKeyRepository.findById(apiKey.getId()).orElse(null);
		assertEquals(revokedAt, reRevokedApiKey.getRevokedAt());

		assertThrows(IllegalOperationException.class, () -> {
			revokedApiKey.activate();
		});
	}

	@Test
	void apiKeyRetrievalTest() {
		byte[] keyHash = new byte[] { 1, 2, 3 };
		ApiClient apiClient = new ApiClient("test", true);
		ApiKey apiKey = new ApiKey(keyHash, apiClient, ApiKeyStatus.ACTIVE);

		apiClientRepository.save(apiClient);
		apiKeyRepository.save(apiKey);

		entityManager.flush();
		entityManager.clear();

		List<ApiKey> findByClientId = apiKeyRepository.findByApiClient_Id(apiClient.getId());
		assertNotEquals(0, findByClientId.size());
		assertEquals(apiKey.getId(), findByClientId.get(0).getId());

		ApiKey findByIdAndClientId = apiKeyRepository.findByIdAndApiClient_Id(apiKey.getId(), apiClient.getId()).orElse(null);
		assertNotNull(findByIdAndClientId);

		ApiKey notFound = apiKeyRepository.findByIdAndApiClient_Id(apiKey.getId(), UUIDGenerator.generateUUIDv7()).orElse(null);
		assertNull(notFound);
	}
}
