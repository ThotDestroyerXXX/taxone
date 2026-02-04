
CREATE TABLE projects (
    id              BINARY(16)      PRIMARY KEY,
    workspace_id    BINARY(16)      NOT NULL,
    owner_id        BINARY(16)      NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT            NOT NULL,
    project_key     VARCHAR(255)    NOT NULL,
    status          ENUM(
                    'ACTIVE',
                    'ARCHIVED',
                    'ON_HOLD'
                    )               NOT NULL DEFAULT 'ACTIVE',
    priority        ENUM(
                    'LOW',
                    'MEDIUM',
                    'HIGH',
                    'CRITICAL'
                    )               NOT NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    color           VARCHAR(10)     NOT NULL,
    is_public       BIT             NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    FOREIGN KEY (owner_id) REFERENCES users(id),

    UNIQUE (workspace_id, project_key)

)