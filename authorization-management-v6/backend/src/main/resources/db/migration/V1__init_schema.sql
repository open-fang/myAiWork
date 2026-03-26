-- V1__init_schema.sql
-- 授权书管理系统数据库表结构初始化脚本

-- 1. 授权书主表
CREATE TABLE IF NOT EXISTS auth_letter (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    auth_object_level VARCHAR(500),
    applicable_region VARCHAR(2000),
    auth_publish_level VARCHAR(500),
    auth_publish_org VARCHAR(2000),
    publish_year INT,
    summary VARCHAR(4000),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE auth_letter IS '授权书主表';
COMMENT ON COLUMN auth_letter.id IS '主键ID';
COMMENT ON COLUMN auth_letter.name IS '授权书名称';
COMMENT ON COLUMN auth_letter.auth_object_level IS '授权对象层级，JSON数组格式';
COMMENT ON COLUMN auth_letter.applicable_region IS '适用区域，JSON数组格式';
COMMENT ON COLUMN auth_letter.auth_publish_level IS '授权发布层级，JSON数组格式';
COMMENT ON COLUMN auth_letter.auth_publish_org IS '授权发布组织，JSON数组格式';
COMMENT ON COLUMN auth_letter.publish_year IS '授权书发布年份';
COMMENT ON COLUMN auth_letter.summary IS '授权书内容摘要';
COMMENT ON COLUMN auth_letter.status IS '状态：DRAFT-草稿，PUBLISHED-已发布，INVALID-已失效';

CREATE INDEX idx_auth_letter_name ON auth_letter(name);
CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);
CREATE INDEX idx_auth_letter_created_time ON auth_letter(created_time);

-- 2. 场景表
CREATE TABLE IF NOT EXISTS auth_letter_scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    scene_name VARCHAR(200) NOT NULL,
    industry VARCHAR(2000),
    business_scene VARCHAR(200),
    decision_level VARCHAR(200),
    specific_rule VARCHAR(1000),
    questionnaire TEXT,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE auth_letter_scene IS '场景表';
COMMENT ON COLUMN auth_letter_scene.id IS '主键ID';
COMMENT ON COLUMN auth_letter_scene.auth_letter_id IS '授权书ID';
COMMENT ON COLUMN auth_letter_scene.scene_name IS '场景名称';
COMMENT ON COLUMN auth_letter_scene.industry IS '产业，JSON数组格式';
COMMENT ON COLUMN auth_letter_scene.business_scene IS '业务场景';
COMMENT ON COLUMN auth_letter_scene.decision_level IS '决策层级';
COMMENT ON COLUMN auth_letter_scene.specific_rule IS '具体规则';
COMMENT ON COLUMN auth_letter_scene.questionnaire IS '问卷设计，JSON格式';

CREATE INDEX idx_scene_auth_letter_id ON auth_letter_scene(auth_letter_id);

-- 3. 规则表
CREATE TABLE IF NOT EXISTS auth_letter_rule (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    rule_name VARCHAR(200),
    logic_type VARCHAR(20) NOT NULL DEFAULT 'AND',
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE auth_letter_rule IS '规则表';
COMMENT ON COLUMN auth_letter_rule.id IS '主键ID';
COMMENT ON COLUMN auth_letter_rule.scene_id IS '场景ID';
COMMENT ON COLUMN auth_letter_rule.rule_name IS '规则名称';
COMMENT ON COLUMN auth_letter_rule.logic_type IS '逻辑类型：AND-且，OR-或';

CREATE INDEX idx_rule_scene_id ON auth_letter_rule(scene_id);

-- 4. 规则条件表
CREATE TABLE IF NOT EXISTS auth_letter_rule_condition (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    parent_condition_id BIGINT,
    field_code VARCHAR(100) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    compare_type VARCHAR(50) NOT NULL,
    compare_field_code VARCHAR(100),
    compare_value VARCHAR(500),
    compare_unit VARCHAR(50),
    logic_type VARCHAR(20) NOT NULL DEFAULT 'AND',
    is_group SMALLINT NOT NULL DEFAULT 0,
    group_logic_type VARCHAR(20),
    sort_order INT NOT NULL DEFAULT 0,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE auth_letter_rule_condition IS '规则条件表';
COMMENT ON COLUMN auth_letter_rule_condition.id IS '主键ID';
COMMENT ON COLUMN auth_letter_rule_condition.rule_id IS '规则ID';
COMMENT ON COLUMN auth_letter_rule_condition.parent_condition_id IS '父条件ID';
COMMENT ON COLUMN auth_letter_rule_condition.field_code IS '规则字段编码';
COMMENT ON COLUMN auth_letter_rule_condition.operator IS '运算符：>, <, =, >=, <=, !=';
COMMENT ON COLUMN auth_letter_rule_condition.compare_type IS '对比类型：FIELD-字段，NUMBER-数值，TEXT-文本，RATIO-比例';
COMMENT ON COLUMN auth_letter_rule_condition.compare_field_code IS '对比字段编码';
COMMENT ON COLUMN auth_letter_rule_condition.compare_value IS '对比值';
COMMENT ON COLUMN auth_letter_rule_condition.compare_unit IS '计量单位';
COMMENT ON COLUMN auth_letter_rule_condition.logic_type IS '与上一条件的连接逻辑';
COMMENT ON COLUMN auth_letter_rule_condition.is_group IS '是否为条件组：0-否，1-是';
COMMENT ON COLUMN auth_letter_rule_condition.group_logic_type IS '条件组内部逻辑';
COMMENT ON COLUMN auth_letter_rule_condition.sort_order IS '排序序号';

CREATE INDEX idx_condition_rule_id ON auth_letter_rule_condition(rule_id);
CREATE INDEX idx_condition_parent_id ON auth_letter_rule_condition(parent_condition_id);

-- 5. 附件表
CREATE TABLE IF NOT EXISTS auth_letter_attachment (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    doc_id VARCHAR(100) NOT NULL,
    doc_name VARCHAR(500) NOT NULL,
    doc_type VARCHAR(50) NOT NULL,
    is_encrypted SMALLINT NOT NULL DEFAULT 0,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE auth_letter_attachment IS '附件表';
COMMENT ON COLUMN auth_letter_attachment.id IS '主键ID';
COMMENT ON COLUMN auth_letter_attachment.auth_letter_id IS '授权书ID';
COMMENT ON COLUMN auth_letter_attachment.doc_id IS '文档ID';
COMMENT ON COLUMN auth_letter_attachment.doc_name IS '文档名称';
COMMENT ON COLUMN auth_letter_attachment.doc_type IS '文档类型';
COMMENT ON COLUMN auth_letter_attachment.is_encrypted IS '是否加密：0-否，1-是';

CREATE INDEX idx_attachment_auth_letter_id ON auth_letter_attachment(auth_letter_id);

-- 6. 操作日志表
CREATE TABLE IF NOT EXISTS auth_letter_log (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    operation VARCHAR(100) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_letter_log IS '操作日志表';
COMMENT ON COLUMN auth_letter_log.id IS '主键ID';
COMMENT ON COLUMN auth_letter_log.auth_letter_id IS '授权书ID';
COMMENT ON COLUMN auth_letter_log.operation IS '操作类型';
COMMENT ON COLUMN auth_letter_log.operator IS '操作人';
COMMENT ON COLUMN auth_letter_log.operation_time IS '操作时间';

CREATE INDEX idx_log_auth_letter_id ON auth_letter_log(auth_letter_id);
CREATE INDEX idx_log_operation_time ON auth_letter_log(operation_time);

-- 7. 规则参数配置表
CREATE TABLE IF NOT EXISTS rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    business_object VARCHAR(200),
    value_logic VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    data_type VARCHAR(50) NOT NULL,
    reference_field_id BIGINT,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE rule_param IS '规则参数配置表';
COMMENT ON COLUMN rule_param.id IS '主键ID';
COMMENT ON COLUMN rule_param.name IS '名称';
COMMENT ON COLUMN rule_param.name_en IS '名称英文';
COMMENT ON COLUMN rule_param.business_object IS '业务对象';
COMMENT ON COLUMN rule_param.value_logic IS '取值逻辑';
COMMENT ON COLUMN rule_param.status IS '状态：ACTIVE-生效，INACTIVE-失效';
COMMENT ON COLUMN rule_param.data_type IS '数据类型：TEXT-文本，NUMBER-数值，FIELD-比对字段，RATIO-比率';
COMMENT ON COLUMN rule_param.reference_field_id IS '关联字段ID';

CREATE INDEX idx_rule_param_name ON rule_param(name);
CREATE INDEX idx_rule_param_name_en ON rule_param(name_en);
CREATE INDEX idx_rule_param_status ON rule_param(status);

-- 8. 下拉类型表
CREATE TABLE IF NOT EXISTS lookup_type (
    id BIGSERIAL PRIMARY KEY,
    type_code VARCHAR(100) NOT NULL UNIQUE,
    type_name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE lookup_type IS '下拉类型表';
COMMENT ON COLUMN lookup_type.id IS '主键ID';
COMMENT ON COLUMN lookup_type.type_code IS '类型编码';
COMMENT ON COLUMN lookup_type.type_name IS '类型名称';
COMMENT ON COLUMN lookup_type.description IS '描述';

-- 9. 下拉值表
CREATE TABLE IF NOT EXISTS lookup_value (
    id BIGSERIAL PRIMARY KEY,
    type_id BIGINT NOT NULL,
    value_code VARCHAR(100) NOT NULL,
    value_name VARCHAR(200) NOT NULL,
    parent_code VARCHAR(100),
    level INT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE lookup_value IS '下拉值表';
COMMENT ON COLUMN lookup_value.id IS '主键ID';
COMMENT ON COLUMN lookup_value.type_id IS '类型ID';
COMMENT ON COLUMN lookup_value.value_code IS '值编码';
COMMENT ON COLUMN lookup_value.value_name IS '值名称';
COMMENT ON COLUMN lookup_value.parent_code IS '父节点编码';
COMMENT ON COLUMN lookup_value.level IS '层级';
COMMENT ON COLUMN lookup_value.sort_order IS '排序序号';
COMMENT ON COLUMN lookup_value.status IS '状态：ACTIVE-生效，INACTIVE-失效';

CREATE INDEX idx_lookup_value_type_id ON lookup_value(type_id);
CREATE INDEX idx_lookup_value_parent_code ON lookup_value(parent_code);