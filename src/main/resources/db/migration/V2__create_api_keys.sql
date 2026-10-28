CREATE TABLE api_keys (
    id UUID PRIMARY KEY,
    key_hash BYTEA NOT NULL,
    api_client_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'active',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ DEFAULT NULL,
    revoked_at TIMESTAMPTZ DEFAULT NULL,

    CONSTRAINT fk_api_key_api_client
        FOREIGN KEY (api_client_id)
        REFERENCES api_clients(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_api_key_status
        CHECK (status IN ('active', 'inactive'))
);