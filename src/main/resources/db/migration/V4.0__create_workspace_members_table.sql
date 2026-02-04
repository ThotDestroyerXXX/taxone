
CREATE TABLE workspace_members (
    id              BINARY(16)      PRIMARY KEY,
    workspace_id    BINARY(16)      NOT NULL,
    user_id         BINARY(16)      NOT NULL,
    invited_by      BINARY(16)      NOT NULL,
    member_type     ENUM('OWNER',
                        'ADMIN',
                        'MEMBER',
                        'VIEWER')   NOT NULL,
    joined_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (invited_by) REFERENCES users(id)
)