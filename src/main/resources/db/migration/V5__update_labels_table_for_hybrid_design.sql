-- Update labels table for hybrid design (global + project-specific labels)

-- Add project_id column (nullable - null means global label)
ALTER TABLE labels ADD COLUMN project_id BIGINT;
ALTER TABLE labels ADD CONSTRAINT fk_label_project 
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- Add created_by column
ALTER TABLE labels ADD COLUMN created_by BIGINT NOT NULL DEFAULT 1;
ALTER TABLE labels ADD CONSTRAINT fk_label_created_by 
    FOREIGN KEY (created_by) REFERENCES users(id);

-- Add created_at column
ALTER TABLE labels ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Add unique constraints
-- Global labels: unique per workspace
CREATE UNIQUE INDEX idx_labels_workspace_global_unique 
    ON labels(workspace_id, name) WHERE project_id IS NULL;

-- Project labels: unique per project
CREATE UNIQUE INDEX idx_labels_project_unique 
    ON labels(project_id, name) WHERE project_id IS NOT NULL;

-- Add indexes
CREATE INDEX idx_labels_project ON labels(project_id);
CREATE INDEX idx_labels_workspace_global ON labels(workspace_id) WHERE project_id IS NULL;
