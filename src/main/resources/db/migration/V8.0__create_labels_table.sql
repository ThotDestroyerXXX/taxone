
CREATE TABLE labels (
    id              BINARY(16)      PRIMARY KEY,
    workspace_id    BINARY(16)      NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    color           VARCHAR(255)    NOT NULL,
    description     TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (workspace_id) REFERENCES workspaces(id)
)