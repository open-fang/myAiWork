-- V1__init_schema.sql
-- Authorization Letter Management System V4
-- 初始化数据库表结构

-- 授权书主表
CREATE TABLE auth_letter (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    auth_target_level TEXT,
    applicable_region TEXT,
    auth_publish_level TEXT,
    auth_publish_org TEXT,
    publish_year INTEGER,
    content_summary TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP
);

CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);

COMMENT ON TABLE auth_letter IS '授权书主表';
COMMENT ON COLUMN auth_letter.code IS '授权书编码';
COMMENT ON COLUMN auth_letter.name IS '授权书名称';
COMMENT ON COLUMN auth_letter.status IS '状态：DRAFT-草稿, PUBLISHED-已发布, EXPIRED-已失效';
COMMENT ON COLUMN auth_letter.auth_target_level IS '授权对象层级（JSON数组）';
COMMENT ON COLUMN auth_letter.applicable_region IS '适用区域（JSON数组）';
COMMENT ON COLUMN auth_letter.auth_publish_level IS '授权发布层级（JSON数组）';
COMMENT ON COLUMN auth_letter.auth_publish_org IS '授权发布组织（JSON数组）';
COMMENT ON COLUMN auth_letter.publish_year IS '授权书发布年份';
COMMENT ON COLUMN auth_letter.content_summary IS '授权书内容摘要';

-- 附件表
CREATE TABLE auth_attachment (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    doc_id VARCHAR(100),
    doc_name VARCHAR(200) NOT NULL,
    doc_type VARCHAR(50),
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachment_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

CREATE INDEX idx_attachment_auth_letter_id ON auth_attachment(auth_letter_id);

COMMENT ON TABLE auth_attachment IS '授权书附件表';
COMMENT ON COLUMN auth_attachment.doc_id IS '文档ID';
COMMENT ON COLUMN auth_attachment.doc_name IS '文档名称';
COMMENT ON COLUMN auth_attachment.doc_type IS '文档类型';
COMMENT ON COLUMN auth_attachment.is_encrypted IS '是否加密';

-- 场景表
CREATE TABLE auth_scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    scene_name VARCHAR(100) NOT NULL,
    industry TEXT,
    business_scenario VARCHAR(100),
    decision_level VARCHAR(50),
    rule_detail TEXT,
    condition_groups TEXT,
    order_index INTEGER DEFAULT 0,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scene_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

CREATE INDEX idx_scene_auth_letter_id ON auth_scene(auth_letter_id);

COMMENT ON TABLE auth_scene IS '授权场景表';
COMMENT ON COLUMN auth_scene.scene_name IS '场景名称';
COMMENT ON COLUMN auth_scene.industry IS '产业（JSON数组）';
COMMENT ON COLUMN auth_scene.business_scenario IS '业务场景';
COMMENT ON COLUMN auth_scene.decision_level IS '决策层级';
COMMENT ON COLUMN auth_scene.rule_detail IS '具体规则';
COMMENT ON COLUMN auth_scene.condition_groups IS '条件组配置（JSON）';

-- 规则参数表
CREATE TABLE rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    data_type VARCHAR(30) NOT NULL,
    business_mappings TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_param_status ON rule_param(status);
CREATE INDEX idx_rule_param_name ON rule_param(name);

COMMENT ON TABLE rule_param IS '规则参数配置表';
COMMENT ON COLUMN rule_param.name IS '名称';
COMMENT ON COLUMN rule_param.name_en IS '名称英文';
COMMENT ON COLUMN rule_param.status IS '状态：ACTIVE-生效, INACTIVE-失效';
COMMENT ON COLUMN rule_param.data_type IS '数据类型：TEXT-文本, NUMBER-数值, COMPARE_FIELD-比对字段, RATIO-比率';
COMMENT ON COLUMN rule_param.business_mappings IS '业务对象与取值逻辑映射（JSON）';

-- 操作日志表
CREATE TABLE auth_operation_log (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL,
    operator VARCHAR(100),
    operated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

CREATE INDEX idx_log_auth_letter_id ON auth_operation_log(auth_letter_id);

COMMENT ON TABLE auth_operation_log IS '操作日志表';
COMMENT ON COLUMN auth_operation_log.operation IS '操作类型：CREATE-创建, UPDATE-更新, PUBLISH-发布, EXPIRE-失效, DELETE-删除';
COMMENT ON COLUMN auth_operation_log.operator IS '操作人';