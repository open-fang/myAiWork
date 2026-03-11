-- V3__add_auth_letter_fields.sql
-- Add additional fields to auth_letter table

-- Add updated_by column if not exists
ALTER TABLE auth_letter ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
COMMENT ON COLUMN auth_letter.updated_by IS '更新人';

-- Add content_summary column if not exists
ALTER TABLE auth_letter ADD COLUMN IF NOT EXISTS content_summary TEXT;
COMMENT ON COLUMN auth_letter.content_summary IS '授权书内容摘要';

-- Add scene additional fields
ALTER TABLE scene ADD COLUMN IF NOT EXISTS industry VARCHAR(500);
ALTER TABLE scene ADD COLUMN IF NOT EXISTS business_scenario VARCHAR(100);
ALTER TABLE scene ADD COLUMN IF NOT EXISTS decision_level_code VARCHAR(50);
ALTER TABLE scene ADD COLUMN IF NOT EXISTS rule_detail TEXT;
ALTER TABLE scene ADD COLUMN IF NOT EXISTS conditions JSONB;
COMMENT ON COLUMN scene.industry IS '产业代码（JSON数组）';
COMMENT ON COLUMN scene.business_scenario IS '业务场景代码';
COMMENT ON COLUMN scene.decision_level_code IS '决策层级代码';
COMMENT ON COLUMN scene.rule_detail IS '具体规则描述';
COMMENT ON COLUMN scene.conditions IS '条件配置（JSON格式）';