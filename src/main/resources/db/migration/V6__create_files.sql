CREATE TABLE files (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(255) NOT NULL,
    storage_configuration_id UUID NOT NULL,
    storage_key VARCHAR(1024) NOT NULL,
    upload_timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    api_client_id UUID NOT NULL,

    CONSTRAINT fk_files_storage_configuration
        FOREIGN KEY (storage_configuration_id)
        REFERENCES storage_configurations(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_files_api_client
        FOREIGN KEY (api_client_id)
        REFERENCES api_clients(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_file_size_bytes
        CHECK (file_size_bytes >= 0),
    CONSTRAINT unique_storage_config_storage_key
        UNIQUE (storage_configuration_id, storage_key)
);