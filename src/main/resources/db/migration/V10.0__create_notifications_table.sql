
CREATE TABLE notifications (
    id                  BINARY(16)                  PRIMARY KEY,
    user_id             BINARY(16)                  NOT NULL,
    message             TEXT                        NOT NULL,
    is_read             BIT                         NOT NULL DEFAULT FALSE,
    notification_type   ENUM (
                        'TASK_ASSIGNED',
                        'TASK_UNASSIGNED',
                        'TASK_STATUS_CHANGED',
                        'TASK_PRIORITY_CHANGED',
                        'TASK_DUE_DATE_CHANGED',
                        'TASK_COMPLETED',
                        'TASK_OVERDUE',
                        'TASK_DUE_SOON',
                        'PROJECT_ASSIGNED',
                        'PROJECT_REMOVED',
                        'PROJECT_ROLE_CHANGED',
                        'WORKSPACE_INVITED',
                        'WORKSPACE_REMOVED',
                        'WORKSPACE_ROLE_CHANGED'
                        )                           NOT NULL,
    created_at          TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at             TIMESTAMP                   NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
)