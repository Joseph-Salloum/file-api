# Storage Configuration Design

## 1. Proposed Fields

### 1.1 storage_configurations table

|     Field     | PostgreSQL Type | Nullable |  Default  | Constraints |               Reason                |
|      id       |      UUID       |    No    |    Non    | Primary Key |   identifying each configuration    |
|     name      |  VARCHAR(255)   |    No    |    Non    |   Unique    |makes configuration management easier|
| provider_type |  VARCHAR(255)   |    No    |    Non    | IN ('LOCAL', 'S3') |   distinguish between local and S3  |
|    status     |  VARCHAR(32)    |    No    | 'READ_WRITE' | IN ('READ_WRITE', 'READ_ONLY', 'DISABLED') | the status of the configuration |

### 1.2 local_storage_configurations table

|     Field     | PostgreSQL Type | Nullable |  Default  | Constraints |               Reason                |
|      id       |      UUID       |    No    |    Non    |    PK, FK   |identifying each local storage config|
|   root_dir    |      TEXT       |    No    |    Non    |     Non     |     path the the root directory     |

### 1.2 s3_storage_configurations table

|     Field     | PostgreSQL Type | Nullable |  Default  | Constraints |               Reason                |
|      id       |      UUID       |    No    |    Non    |    PK, FK   | identifying each s3 storage config  |
|    bucket     |      TEXT       |    No    |    Non    |     Non     |           the bucket name           |
|    region     |      TEXT       |    No    |    Non    |     Non     |              the region             |
|   endpoint    |      TEXT       |   Yes    |    Non    |     Non     |          S3 API endpoint            |
| credential_reference |      TEXT       |   Yes    |    Non    |     Non     | a reference to the external credentials |
| path_style_access |    BOOLEAN     |    No    |   FALSE   |     Non     | identifying if this provider uses path style or virtual-hosted style |

## 2. Design Decisions

### 2.1 Changing Existing Storage Config's Provider
Should the system allow changing an existing storage configuration's provider type from LOCAL to S3?
no it shouldn't, because the files that are already stored in that configuration will be lost(the application will lose the configuration information needed to locate them)

### 2.2 Changing The S3 Bucket or Local Root Directory of an Existing Storage Configuration
Will changing the S3 bucket or local root directory of an existing storage configuration cause the same kind of problem as changing the provider type?
yes it will, so also this field has to be immutable

## 3. Assumptions

- Provider-specific tables share the parent's primary key.
- Storage configurations have three states: READ_WRITE, READ_ONLY, and DISABLED.
- S3 credentials are referenced, not stored as plaintext.
- Existing files prevent deletion of their referenced storage configuration.
- The application validates that the provider-specific record matches the parent's provider type.
- When credential_reference is NULL, use the SDK's default credential provider chain.
- When it is non-null, resolve the named credentials from external configuration.
- If the named credentials are unavailable, reject the operation rather than silently falling back to another identity.