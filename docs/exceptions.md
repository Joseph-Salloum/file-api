# Exceptions

## HTTP 403 (Forbidden)

### 1. IllegalOperationException

- Indicates that an operation is being performed on an entity/object that is not in a suitable state, or that the requested operation is not permitted.

Examples:
- Trying to activate a revoked API key.
- Trying to create an API key for a disabled API client.

## HTTP 404 (Not Found)

### 1. ClientNotFoundException

- Indicates that the requested API client does not exist.

Examples:
- Trying to create an API key using a nonexistent API client UUID.

## HTTP 422 (Unprocessable Content)

### 1. InvalidTimestampException

- Indicates that a provided timestamp violates the application's timestamp validation rules.

Examples:
- Trying to create an API key with an `expires_at` value that is not strictly later than the current timestamp.

Note: If `expires_at` is null, the API key does not expire.