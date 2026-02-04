
CREATE TABLE workspaces (
    id          BINARY(16)      PRIMARY KEY,
    owner_id    BINARY(16)      NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    description TEXT            NOT NULL,
    slug        VARCHAR(255)    NOT NULL UNIQUE,
    logo_url    VARCHAR(255),
    is_active   BIT             NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (owner_id) REFERENCES users(id)
)