CREATE TABLE bulk_jobs (
    id VARCHAR(36) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    total_items INT,
    processed_items INT DEFAULT 0,
    failed_items INT DEFAULT 0,
    job_type VARCHAR(50) NOT NULL,
    error_message TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_bulk_jobs_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_bulk_jobs_status ON bulk_jobs(status);
CREATE INDEX idx_bulk_jobs_created_by ON bulk_jobs(created_by);
