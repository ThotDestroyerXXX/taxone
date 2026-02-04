
CREATE TABLE project_members (
    id          BINARY(16)      PRIMARY KEY,
    project_id  BINARY(16)      NOT NULL,
    user_id     BINARY(16)      NOT NULL,
    added_by    BINARY(16)      NOT NULL,
    member_type ENUM(
                'PROJECT_LEAD',
                'CONTRIBUTOR',
                'VIEWER'
                )               NOT NULL DEFAULT 'VIEWER',
    added_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (added_by) REFERENCES users(id)
)