
CREATE TABLE users (
    id                  BINARY(16)      PRIMARY KEY,
    email               VARCHAR(255)    NOT NULL UNIQUE,
    password            VARCHAR(255)    NOT NULL,
    first_name          VARCHAR(100)    NOT NULL,
    last_name           VARCHAR(100)    NOT NULL,
    profile_picture_url VARCHAR(255),
    phone_number        VARCHAR(15)     UNIQUE,
    is_active           BIT             NOT NULL DEFAULT TRUE,
    email_verified      BIT             NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
)