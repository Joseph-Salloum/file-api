CREATE TABLE storage_configurations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    provider_type VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'READ_WRITE',

    CONSTRAINT chk_storage_configuration_provider_type
        CHECK (provider_type IN ('LOCAL', 'S3')),
    CONSTRAINT chk_storage_configuration_status
        CHECK (status IN ('READ_WRITE', 'READ_ONLY', 'DISABLED'))
);