
CREATE TABLE task_labels (
    id          BINARY(16)  PRIMARY KEY,
    task_id     BINARY(16)  NOT NULL,
    label_id    BINARY(16)  NOT NULL,
    added_by    BINARY(16)  NOT NULL,
    added_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (label_id) REFERENCES labels(id)
)