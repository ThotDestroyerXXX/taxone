
CREATE TABLE tokens (
    id          BINARY(16)      PRIMARY KEY,
    user_id     BINARY(16)      NOT NULL,
    token       VARCHAR(255)    NOT NULL,
    token_type  VARCHAR(20)     NOT NULL DEFAULT 'BEARER',
    expired     BOOLEAN         DEFAULT FALSE,
    revoked     BOOLEAN         DEFAULT FALSE,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP       NULL,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);