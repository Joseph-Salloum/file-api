# API Key Design

## 1. Proposed Fields

|     Field     | PostgreSQL Type | Nullable |  Default  | Constraints |               Reason                |
|      id       |      UUID       |    No    |    Non    | Primary Key |      identifying each API-Key       |
|   key_hash    |      BYTEA      |    No    |    Non    |     Non     |          the secret API key         |
| api_client_id |      UUID       |    No    |    Non    | Foreign Key | determine which client owns the key |
|    status     |  VARCHAR(32)    |    No    |  'active' | IN ('active', 'inactive') | the status of the key |
|  created_at   |     TSWTZ       |    No    |  curr ts  |     Non     | the timestamp the key was issued at |
|  expires_at   |     TSWTZ       |   Yes    |   Null    |     Non     |  the timestamp the key expires at   |
|  revoked_at   |     TSWTZ       |   Yes    |   Null    |     Non     |  the timestamp the key is revoked   |


## 2. Design Decisions

### 2.1 Expiration
How will the application determine whether a key has expired?
by checking for this condition: (expires_at IS NULL OR expires_at > current_time) if it returns true, then the key is not expired or else it is

### 2.2 Key Validation
how will the system determines whether a key is valid?
by performing these checks:
1- searching for a row in this table with the extracted id
2- checking if the key is inactive or revoked
3- checking if the key is expired
4- hashing the key and compare it with the stored hash
5- checking whether the API client associated with the key is enabled
if the validation failed at any point, I will assume that the key is invalid and return HTTP 401 (unauthorized)

## 3. Assumptions

- the API key format: fa_<key_id>.<random_secret>
- will use java's SecureRandom to generate the secret, SHA-256 algorithm to hash the api key before storing it.
- key_hash field contains the hash of the entire API key.
- if expires_at value was NULL, this means that this key has no expiration time(the key does not expire).
- if revoked_at is null then the key has not been revoked, or else it is revoked and will never work again.
- the client may need a way to temporarily disable a specific key, in this case he can use the status column.

## 4. Key Creation Business Rules

- The requested API client must exist and be enabled.
- Whenever expires_at is provided, it must be strictly later than the current time. Otherwise, an InvalidTimestampException is thrown and HTTP 422 (Unprocessable Content) is returned.
- Clients that do not exist will cause a ClientNotFoundException and HTTP 404 (Not Found).
- Disabled clients will cause an IllegalOperationException and HTTP 403 (Forbidden).
- Newly created API keys automatically receive the ACTIVE status. The client may change the key's status through a separate operation after creation.

## 5. API Key Creation Workflow

### 5.1 Overall Workflow

- `ApiKeyResponse createApiKey(CreateApiKeyRequest request)` method inside `ApiKeyService` receives a `CreateApiKeyRequest` object from the controller that's calling it and returns an `ApiKeyResponse` object.
- `CreateApiKeyRequest` and `ApiKeyResponse` internal structure:
    ```java
        public class CreateApiKeyRequest {
            @NotNull
            private UUID apiClientId;
            private OffsetDateTime expiresAt;
        }

        public class ApiKeyResponse {
            private String apiKey;
            private ApiKeyStatus status;
            private OffsetDateTime createdAt;
            private OffsetDateTime expiresAt;
        }
    ```
- The method retrieves the client from the db using `ApiClientRepository`, it verifies that the client is enabled, and validates the `expiresAt` timestamp if provided, then it delegates the generation and entity construction to `ApiKeyFactory`, then it persists the entity through `ApiKeyRepository` and constructs an `ApiKeyResponse` response DTO which holds the plaintext API key and returns it to the caller.

### 5.2 Transaction Management

- `createApiKey()` method executes within a db transaction.
- If the method completes successfully, Spring attempts to commit the transaction. By default, unchecked exceptions (RuntimeException and Error) escaping the method trigger a rollback.
- The response object is constructed before the transaction is committed. However, constructing the response does not guarantee that the transaction will commit successfully.
- If the commit fails, the caller receives an exception instead of a successful response. The final transaction outcome depends on the nature of the failure and the transaction manager.
- Note that database rollback does not reverse in-memory operations, such as plaintext API key generation.
### 5.3 Security Considerations

- Plaintext API keys are returned only during creation and can never be retrieved later.
- Only the hashed API key is persisted.
- The plaintext key is obtained from the factory's generation result, not the database entity.