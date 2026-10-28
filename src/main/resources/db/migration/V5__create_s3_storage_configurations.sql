CREATE TABLE s3_storage_configurations (
    id UUID PRIMARY KEY,
    bucket TEXT NOT NULL,
    region TEXT NOT NULL,
    endpoint TEXT,
    credential_reference TEXT,
    path_style_access BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_s3_storage_config_id_storage_config_id
        FOREIGN KEY (id)
        REFERENCES storage_configurations(id)
        ON DELETE CASCADE
);