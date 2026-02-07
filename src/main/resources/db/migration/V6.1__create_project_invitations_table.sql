
CREATE TABLE project_invitations (
    id                  BINARY(16)      PRIMARY KEY,
    project_id          BINARY(16)      NOT NULL,
    email               VARCHAR(255)    NOT NULL,
    invited_by          BINARY(16)      NOT NULL,
    member_type         ENUM(
                        'PROJECT_LEAD',
                        'CONTRIBUTOR',
                        'VIEWER'
                        )               NOT NULL,
    status              ENUM(
                        'PENDING',
                        'ACCEPTED',
                        'DECLINED',
                        'EXPIRED',
                        'CANCELLED'
                        )               NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at        TIMESTAMP       NULL,

    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (invited_by) REFERENCES users(id)
)