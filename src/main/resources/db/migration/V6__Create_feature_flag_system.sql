-- ============================================
-- V6__Create_feature_flag_system.sql
-- Feature Flag & Plan-Based Access Control System
-- ============================================

-- Features catalog (seeded with available features)
CREATE TABLE features (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    default_timing VARCHAR(20) NOT NULL,
    is_system BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Plan configurations (replaces simple enum)
CREATE TABLE plan_configurations (
    id BIGSERIAL PRIMARY KEY,
    plan_tier VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Unique constraint: only one active plan per tier
CREATE UNIQUE INDEX uq_plan_tier_active ON plan_configurations(plan_tier) WHERE is_active = true;

-- Feature enablement per plan (the actual flags)
CREATE TABLE plan_features (
    id BIGSERIAL PRIMARY KEY,
    plan_configuration_id BIGINT NOT NULL REFERENCES plan_configurations(id) ON DELETE CASCADE,
    feature_id BIGINT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    is_enabled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_feature UNIQUE (plan_configuration_id, feature_id)
);

-- Limits per plan
CREATE TABLE plan_limits (
    id BIGSERIAL PRIMARY KEY,
    plan_configuration_id BIGINT NOT NULL REFERENCES plan_configurations(id) ON DELETE CASCADE,
    limit_type VARCHAR(50) NOT NULL,
    limit_value INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_limit UNIQUE (plan_configuration_id, limit_type)
);

-- Update workspace to reference plan configuration
ALTER TABLE workspaces ADD COLUMN plan_configuration_id BIGINT REFERENCES plan_configurations(id);

-- Create indexes for performance
CREATE INDEX idx_plan_features_config_id ON plan_features(plan_configuration_id);
CREATE INDEX idx_plan_limits_config_id ON plan_limits(plan_configuration_id);
CREATE INDEX idx_workspaces_plan_config_id ON workspaces(plan_configuration_id);

-- ============================================
-- Seed Data
-- ============================================

-- Insert features
INSERT INTO features (code, name, description, category, default_timing, is_system) VALUES
    ('audit_log', 'Audit Log', 'Track all changes in workspace', 'SECURITY', 'POST', true),
    ('notifications', 'Notifications', 'Email and push notifications', 'COLLABORATION', 'POST', true),
    ('advanced_search', 'Advanced Search', 'Full-text search indexing', 'PRODUCTIVITY', 'ASYNC', true),
    ('webhooks', 'Webhooks', 'External system integrations', 'INTEGRATION', 'ASYNC', true),
    ('attachments', 'Attachments', 'File upload capability', 'COLLABORATION', 'VALIDATE', true),
    ('bulk_operations', 'Bulk Operations', 'Bulk edit tasks', 'PRODUCTIVITY', 'VALIDATE', true),
    ('project_limits', 'Project Limits', 'Limit number of projects', 'LIMITS', 'VALIDATE', true),
    ('member_limits', 'Member Limits', 'Limit workspace members', 'LIMITS', 'VALIDATE', true),
    ('storage_limits', 'Storage Limits', 'Limit storage usage', 'LIMITS', 'VALIDATE', true);

-- Insert plan configurations
INSERT INTO plan_configurations (plan_tier, name, description, is_active, is_default) VALUES
    ('FREE', 'Free Plan', 'Basic features for individuals', true, true),
    ('TEAM', 'Team Plan', 'Advanced features for teams', true, false),
    ('ENTERPRISE', 'Enterprise Plan', 'Full feature set', true, false);

-- Enable features for FREE plan (notifications, attachments, limits only)
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, f.code IN ('notifications', 'attachments', 'project_limits', 'member_limits', 'storage_limits')
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'FREE';

-- Enable features for TEAM plan (all except audit_log and webhooks)
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, f.code NOT IN ('audit_log', 'webhooks')
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'TEAM';

-- Enable all features for ENTERPRISE plan
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, true
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'ENTERPRISE';

-- Set limits for FREE plan
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', 3 FROM plan_configurations WHERE plan_tier = 'FREE'
UNION ALL
SELECT id, 'MAX_MEMBERS', 5 FROM plan_configurations WHERE plan_tier = 'FREE'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', 100 FROM plan_configurations WHERE plan_tier = 'FREE';

-- Set limits for TEAM plan
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', 50 FROM plan_configurations WHERE plan_tier = 'TEAM'
UNION ALL
SELECT id, 'MAX_MEMBERS', 50 FROM plan_configurations WHERE plan_tier = 'TEAM'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', 10240 FROM plan_configurations WHERE plan_tier = 'TEAM';

-- Set limits for ENTERPRISE plan (-1 = unlimited)
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE'
UNION ALL
SELECT id, 'MAX_MEMBERS', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE';

-- Migrate existing workspaces to use plan configurations
UPDATE workspaces 
SET plan_configuration_id = (
    SELECT id FROM plan_configurations 
    WHERE plan_tier = workspaces.plan_tier AND is_active = true
)
WHERE plan_configuration_id IS NULL;
