-- =====================================================
-- 授权书管理系统 V5 数据库初始化脚本
-- =====================================================

-- 1. 创建下拉列表值表
CREATE TABLE auth_lookup_value (
    id BIGSERIAL PRIMARY KEY,
    type_code VARCHAR(50) NOT NULL,
    item_value VARCHAR(200) NOT NULL,
    label VARCHAR(200) NOT NULL,
    parent_id BIGINT NULL REFERENCES auth_lookup_value(id),
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_lookup_value IS '下拉列表值表';
COMMENT ON COLUMN auth_lookup_value.type_code IS '类型编码';
COMMENT ON COLUMN auth_lookup_value.item_value IS '值';
COMMENT ON COLUMN auth_lookup_value.label IS '显示文本';
COMMENT ON COLUMN auth_lookup_value.parent_id IS '父节点ID';
COMMENT ON COLUMN auth_lookup_value.sort_order IS '排序号';
COMMENT ON COLUMN auth_lookup_value.is_active IS '是否启用';

-- 创建索引
CREATE INDEX idx_lookup_type_code ON auth_lookup_value(type_code);
CREATE INDEX idx_lookup_parent_id ON auth_lookup_value(parent_id);

-- 2. 创建授权书主表
CREATE TABLE auth_letter (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    auth_target_level VARCHAR(500),
    applicable_region VARCHAR(1000),
    auth_publish_level VARCHAR(500),
    auth_publish_org VARCHAR(1000),
    publish_year INTEGER,
    summary VARCHAR(4000),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_letter IS '授权书主表';
COMMENT ON COLUMN auth_letter.name IS '授权书名称';
COMMENT ON COLUMN auth_letter.auth_target_level IS '授权对象层级（JSON数组）';
COMMENT ON COLUMN auth_letter.applicable_region IS '适用区域（JSON数组）';
COMMENT ON COLUMN auth_letter.auth_publish_level IS '授权发布层级（JSON数组）';
COMMENT ON COLUMN auth_letter.auth_publish_org IS '授权发布组织（JSON数组）';
COMMENT ON COLUMN auth_letter.publish_year IS '授权书发布年份';
COMMENT ON COLUMN auth_letter.summary IS '授权书内容摘要';
COMMENT ON COLUMN auth_letter.status IS '状态：DRAFT-草稿, PUBLISHED-已发布, INVALID-已失效';

-- 创建索引
CREATE INDEX idx_letter_status ON auth_letter(status);
CREATE INDEX idx_letter_name ON auth_letter(name);
CREATE INDEX idx_letter_publish_year ON auth_letter(publish_year);

-- 3. 创建附件表
CREATE TABLE auth_attachment (
    id BIGSERIAL PRIMARY KEY,
    letter_id BIGINT NOT NULL REFERENCES auth_letter(id) ON DELETE CASCADE,
    doc_name VARCHAR(500) NOT NULL,
    doc_type VARCHAR(50),
    doc_id VARCHAR(200),
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_attachment IS '附件表';
COMMENT ON COLUMN auth_attachment.letter_id IS '授权书ID';
COMMENT ON COLUMN auth_attachment.doc_name IS '文档名称';
COMMENT ON COLUMN auth_attachment.doc_type IS '文档类型';
COMMENT ON COLUMN auth_attachment.doc_id IS '文档ID';
COMMENT ON COLUMN auth_attachment.is_encrypted IS '是否加密';

-- 创建索引
CREATE INDEX idx_attachment_letter_id ON auth_attachment(letter_id);

-- 4. 创建场景表
CREATE TABLE auth_scene (
    id BIGSERIAL PRIMARY KEY,
    letter_id BIGINT NOT NULL REFERENCES auth_letter(id) ON DELETE CASCADE,
    scene_name VARCHAR(200) NOT NULL,
    industry VARCHAR(1000),
    business_scene VARCHAR(200),
    decision_level VARCHAR(200),
    specific_rule VARCHAR(1000),
    rule_config TEXT,
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_scene IS '场景表';
COMMENT ON COLUMN auth_scene.letter_id IS '授权书ID';
COMMENT ON COLUMN auth_scene.scene_name IS '场景名称';
COMMENT ON COLUMN auth_scene.industry IS '产业（JSON数组）';
COMMENT ON COLUMN auth_scene.business_scene IS '业务场景';
COMMENT ON COLUMN auth_scene.decision_level IS '决策层级';
COMMENT ON COLUMN auth_scene.specific_rule IS '具体规则';
COMMENT ON COLUMN auth_scene.rule_config IS '规则配置（JSON格式）';

-- 创建索引
CREATE INDEX idx_scene_letter_id ON auth_scene(letter_id);

-- 5. 创建规则参数配置表
CREATE TABLE auth_rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    name_en VARCHAR(200),
    business_objects TEXT,
    value_logics TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    data_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_rule_param IS '规则参数配置表';
COMMENT ON COLUMN auth_rule_param.name IS '名称';
COMMENT ON COLUMN auth_rule_param.name_en IS '名称英文';
COMMENT ON COLUMN auth_rule_param.business_objects IS '业务对象（JSON数组）';
COMMENT ON COLUMN auth_rule_param.value_logics IS '取值逻辑（JSON数组）';
COMMENT ON COLUMN auth_rule_param.is_active IS '是否生效';
COMMENT ON COLUMN auth_rule_param.data_type IS '数据类型：TEXT-文本, NUMBER-数值, COMPARE_FIELD-比对字段, RATIO-比率';
COMMENT ON COLUMN auth_rule_param.status IS '状态：ACTIVE-生效, INACTIVE-失效';

-- 创建索引
CREATE INDEX idx_rule_param_status ON auth_rule_param(status);
CREATE INDEX idx_rule_param_name ON auth_rule_param(name);

-- 6. 创建操作日志表
CREATE TABLE auth_operation_log (
    id BIGSERIAL PRIMARY KEY,
    letter_id BIGINT NOT NULL REFERENCES auth_letter(id) ON DELETE CASCADE,
    operation VARCHAR(200) NOT NULL,
    operator VARCHAR(100),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_operation_log IS '操作日志表';
COMMENT ON COLUMN auth_operation_log.letter_id IS '授权书ID';
COMMENT ON COLUMN auth_operation_log.operation IS '操作类型';
COMMENT ON COLUMN auth_operation_log.operator IS '操作人';
COMMENT ON COLUMN auth_operation_log.operation_time IS '操作时间';

-- 创建索引
CREATE INDEX idx_log_letter_id ON auth_operation_log(letter_id);
CREATE INDEX idx_log_operation_time ON auth_operation_log(operation_time);