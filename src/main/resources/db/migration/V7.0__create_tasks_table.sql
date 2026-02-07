
CREATE TABLE tasks (
    id              BINARY(16)      PRIMARY KEY,
    project_id      BINARY(16)      NOT NULL,
    assignee_id     BINARY(16)      NULL,
    reporter_id     BINARY(16)      NOT NULL,
    parent_task_id  BINARY(16)      NULL,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT            NOT NULL,
    task_key        VARCHAR(255)    NOT NULL,
    status          ENUM (
                    'TODO',
                    'IN_PROGRESS',
                    'IN_REVIEW',
                    'DONE',
                    'BLOCKED'
                    )               NOT NULL DEFAULT 'TODO',
    priority        ENUM (
                    'LOW',
                    'MEDIUM',
                    'HIGH',
                    'CRITICAL'
                    )               NOT NULL,
    due_date        DATE,
    estimated_hours INT,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP       NULL,

    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (parent_task_id) REFERENCES tasks(id)
)