CREATE TABLE local_storage_configurations (
    id UUID PRIMARY KEY,
    root_dir TEXT NOT NULL,

    CONSTRAINT fk_local_storage_config_id_storage_config_id
        FOREIGN KEY (id)
        REFERENCES storage_configurations(id)
        ON DELETE CASCADE
);