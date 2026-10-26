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
if the validation failed at any point, I will assume that the key is invalid and return 401 unauthorized

## 3. Assumptions

- the API key format: fa_<key_id>.<random_secret>
- will use java's SecureRandom to generate the secret, SHA-256 algorithm to hash the api key before storing it.
- key_hash field contains the hash of the entire API key.
- if expires_at value was NULL, this means that this key has no expiration time(the key does not expire).
- if revoked_at is null then the key has not been revoked, or else it is revoked and will never work again.
- the client may need a way to temporarily disable a specific key, in this case he can use the status column.