-- V2__add_rule_param_table.sql
-- Add Rule Parameter table for rule field configuration

-- Rule Parameter Table
CREATE TABLE rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    data_type VARCHAR(30) NOT NULL,
    business_mappings TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE rule_param IS '规则参数表 - 用于规则字段配置';
COMMENT ON COLUMN rule_param.name IS '名称';
COMMENT ON COLUMN rule_param.name_en IS '名称英文（唯一）';
COMMENT ON COLUMN rule_param.status IS '状态: ACTIVE-生效, INACTIVE-失效';
COMMENT ON COLUMN rule_param.data_type IS '数据类型: TEXT-文本, NUMBER-数值, COMPARE_FIELD-比对字段, RATIO-比率';
COMMENT ON COLUMN rule_param.business_mappings IS '业务对象与取值逻辑映射（JSON格式）';

-- Create indexes
CREATE INDEX idx_rule_param_status ON rule_param(status);
CREATE INDEX idx_rule_param_name ON rule_param(name);
CREATE INDEX idx_rule_param_name_en ON rule_param(name_en);

-- Apply trigger for updated_at
CREATE TRIGGER update_rule_param_updated_at
    BEFORE UPDATE ON rule_param
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add content_summary column to auth_letter if not exists
ALTER TABLE auth_letter ADD COLUMN IF NOT EXISTS content_summary TEXT;
COMMENT ON COLUMN auth_letter.content_summary IS '授权书内容摘要';