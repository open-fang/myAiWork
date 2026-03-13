-- V1__init_schema.sql
-- Authorization Letter Management System V5
-- Database initialization script for PostgreSQL

-- ============================================
-- 1. Authorization Letter Table
-- ============================================
CREATE TABLE auth_letter (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    auth_target_level VARCHAR(500),
    applicable_region VARCHAR(1000),
    auth_publish_level VARCHAR(500),
    auth_publish_org VARCHAR(1000),
    publish_year INTEGER,
    content_summary TEXT,

    -- IT Audit Fields
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,

    -- Status Related Fields
    published_at TIMESTAMP,
    expired_at TIMESTAMP
);

COMMENT ON TABLE auth_letter IS '授权书主表';
COMMENT ON COLUMN auth_letter.id IS '主键ID';
COMMENT ON COLUMN auth_letter.code IS '授权书编码';
COMMENT ON COLUMN auth_letter.name IS '授权书名称';
COMMENT ON COLUMN auth_letter.status IS '状态: DRAFT-草稿, PUBLISHED-已发布, EXPIRED-已失效';
COMMENT ON COLUMN auth_letter.auth_target_level IS '授权对象层级(JSON数组)';
COMMENT ON COLUMN auth_letter.applicable_region IS '适用区域(JSON数组)';
COMMENT ON COLUMN auth_letter.auth_publish_level IS '授权发布层级(JSON数组)';
COMMENT ON COLUMN auth_letter.auth_publish_org IS '授权发布组织(JSON数组)';
COMMENT ON COLUMN auth_letter.publish_year IS '授权书发布年份';
COMMENT ON COLUMN auth_letter.content_summary IS '授权书内容摘要';

CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);
CREATE INDEX idx_auth_letter_name ON auth_letter(name);
CREATE INDEX idx_auth_letter_code ON auth_letter(code);

-- ============================================
-- 2. Attachment Table
-- ============================================
CREATE TABLE auth_attachment (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    doc_id VARCHAR(100) NOT NULL,
    doc_name VARCHAR(500) NOT NULL,
    doc_type VARCHAR(50),
    file_size BIGINT,
    encrypted BOOLEAN DEFAULT FALSE,

    -- IT Audit Fields
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attachment_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

COMMENT ON TABLE auth_attachment IS '授权书附件表';
COMMENT ON COLUMN auth_attachment.doc_id IS '文档ID(文件上传后生成)';
COMMENT ON COLUMN auth_attachment.doc_name IS '文档名称';
COMMENT ON COLUMN auth_attachment.doc_type IS '文档类型';
COMMENT ON COLUMN auth_attachment.encrypted IS '是否加密';

CREATE INDEX idx_attachment_auth_letter_id ON auth_attachment(auth_letter_id);

-- ============================================
-- 3. Scene Table
-- ============================================
CREATE TABLE auth_scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    industry VARCHAR(1000),
    business_scenario VARCHAR(100),
    decision_level VARCHAR(50),
    rule_detail TEXT,
    condition_groups JSONB,

    -- IT Audit Fields
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_scene_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

COMMENT ON TABLE auth_scene IS '授权场景表';
COMMENT ON COLUMN auth_scene.name IS '场景名称';
COMMENT ON COLUMN auth_scene.industry IS '产业(JSON数组)';
COMMENT ON COLUMN auth_scene.business_scenario IS '业务场景';
COMMENT ON COLUMN auth_scene.decision_level IS '决策层级';
COMMENT ON COLUMN auth_scene.rule_detail IS '具体规则';
COMMENT ON COLUMN auth_scene.condition_groups IS '规则条件组(JSONB)';

CREATE INDEX idx_scene_auth_letter_id ON auth_scene(auth_letter_id);

-- ============================================
-- 4. Rule Parameter Table
-- ============================================
CREATE TABLE auth_rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    data_type VARCHAR(30) NOT NULL,
    business_mappings TEXT,

    -- IT Audit Fields
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_rule_param IS '规则参数配置表';
COMMENT ON COLUMN auth_rule_param.name IS '参数名称';
COMMENT ON COLUMN auth_rule_param.name_en IS '参数英文名称';
COMMENT ON COLUMN auth_rule_param.status IS '状态: ACTIVE-生效, INACTIVE-失效';
COMMENT ON COLUMN auth_rule_param.data_type IS '数据类型: TEXT-文本, NUMBER-数值, FIELD-比对字段, RATIO-比率';
COMMENT ON COLUMN auth_rule_param.business_mappings IS '业务对象与取值逻辑映射(JSON数组)';

CREATE INDEX idx_rule_param_status ON auth_rule_param(status);
CREATE INDEX idx_rule_param_name ON auth_rule_param(name);
CREATE INDEX idx_rule_param_name_en ON auth_rule_param(name_en);

-- ============================================
-- 5. Operation Log Table
-- ============================================
CREATE TABLE auth_operation_log (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_desc VARCHAR(500),

    -- IT Audit Fields
    operator VARCHAR(100),
    operated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_log_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

COMMENT ON TABLE auth_operation_log IS '授权书操作日志表';
COMMENT ON COLUMN auth_operation_log.operation_type IS '操作类型: CREATE, UPDATE, PUBLISH, EXPIRE, DELETE';
COMMENT ON COLUMN auth_operation_log.operation_desc IS '操作描述';

CREATE INDEX idx_log_auth_letter_id ON auth_operation_log(auth_letter_id);
CREATE INDEX idx_log_operated_at ON auth_operation_log(operated_at);

-- ============================================
-- 6. Lookup Table (for dropdown options)
-- ============================================
CREATE TABLE auth_lookup (
    id BIGSERIAL PRIMARY KEY,
    lookup_type VARCHAR(50) NOT NULL,
    lookup_code VARCHAR(100) NOT NULL,
    lookup_name VARCHAR(200) NOT NULL,
    parent_code VARCHAR(100),
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- IT Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_lookup_type_code UNIQUE (lookup_type, lookup_code)
);

COMMENT ON TABLE auth_lookup IS '值列表配置表';
COMMENT ON COLUMN auth_lookup.lookup_type IS '值列表类型';
COMMENT ON COLUMN auth_lookup.lookup_code IS '值编码';
COMMENT ON COLUMN auth_lookup.lookup_name IS '值名称';
COMMENT ON COLUMN auth_lookup.parent_code IS '父级编码(用于树形结构)';
COMMENT ON COLUMN auth_lookup.sort_order IS '排序号';

CREATE INDEX idx_lookup_type ON auth_lookup(lookup_type);
CREATE INDEX idx_lookup_parent ON auth_lookup(parent_code);

-- ============================================
-- 7. Insert Initial Lookup Data
-- ============================================

-- 授权对象层级
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('AUTH_TARGET_LEVEL', 'LEVEL_1', '集团', 1),
('AUTH_TARGET_LEVEL', 'LEVEL_2', '区域', 2),
('AUTH_TARGET_LEVEL', 'LEVEL_3', '代表处', 3);

-- 授权发布层级
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_1', '一级发布', 1),
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_2', '二级发布', 2),
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_3', '三级发布', 3);

-- 组织树 - 机关
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('ORG_TREE', 'ORG_HEADQUARTERS', '机关', NULL, 1);

-- 组织树 - 地区部
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('ORG_TREE', 'ORG_REGION_1', '地区部A', 'ORG_HEADQUARTERS', 1),
('ORG_TREE', 'ORG_REGION_2', '地区部B', 'ORG_HEADQUARTERS', 2);

-- 组织树 - 代表处
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('ORG_TREE', 'ORG_REP_1', '代表处A1', 'ORG_REGION_1', 1),
('ORG_TREE', 'ORG_REP_2', '代表处A2', 'ORG_REGION_1', 2),
('ORG_TREE', 'ORG_REP_3', '代表处B1', 'ORG_REGION_2', 1),
('ORG_TREE', 'ORG_REP_4', '代表处B2', 'ORG_REGION_2', 2);

-- 适用区域树 - 机关
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('APPLICABLE_REGION', 'REG_HEADQUARTERS', '机关', NULL, 1);

-- 适用区域树 - 地区部
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('APPLICABLE_REGION', 'REG_REGION_1', '地区部A', 'REG_HEADQUARTERS', 1),
('APPLICABLE_REGION', 'REG_REGION_2', '地区部B', 'REG_HEADQUARTERS', 2);

-- 适用区域树 - 代表处
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('APPLICABLE_REGION', 'REG_REP_1', '代表处A1', 'REG_REGION_1', 1),
('APPLICABLE_REGION', 'REG_REP_2', '代表处A2', 'REG_REGION_1', 2),
('APPLICABLE_REGION', 'REG_REP_3', '代表处B1', 'REG_REGION_2', 1),
('APPLICABLE_REGION', 'REG_REP_4', '代表处B2', 'REG_REGION_2', 2);

-- 产业树
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('INDUSTRY', 'IND_TECH', '科技产业', NULL, 1),
('INDUSTRY', 'IND_FIN', '金融产业', NULL, 2),
('INDUSTRY', 'IND_MANU', '制造产业', NULL, 3);

-- 产业子类
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, parent_code, sort_order) VALUES
('INDUSTRY', 'IND_TECH_SW', '软件', 'IND_TECH', 1),
('INDUSTRY', 'IND_TECH_HW', '硬件', 'IND_TECH', 2),
('INDUSTRY', 'IND_FIN_BANK', '银行', 'IND_FIN', 1),
('INDUSTRY', 'IND_FIN_INSUR', '保险', 'IND_FIN', 2);

-- 业务场景
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('BUSINESS_SCENARIO', 'BIZ_SALE', '销售', 1),
('BUSINESS_SCENARIO', 'BIZ_PURCHASE', '采购', 2),
('BUSINESS_SCENARIO', 'BIZ_CONTRACT', '合同', 3);

-- 决策层级
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('DECISION_LEVEL', 'DEC_LEVEL_1', '一级决策', 1),
('DECISION_LEVEL', 'DEC_LEVEL_2', '二级决策', 2),
('DECISION_LEVEL', 'DEC_LEVEL_3', '三级决策', 3);

-- 文档类型
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('DOC_TYPE', 'DOC_PDF', 'PDF文档', 1),
('DOC_TYPE', 'DOC_WORD', 'Word文档', 2),
('DOC_TYPE', 'DOC_EXCEL', 'Excel文档', 3),
('DOC_TYPE', 'DOC_IMAGE', '图片', 4),
('DOC_TYPE', 'DOC_OTHER', '其他', 5);

-- 运算符
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('OPERATOR', 'OP_GT', '>', 1),
('OPERATOR', 'OP_LT', '<', 2),
('OPERATOR', 'OP_EQ', '=', 3),
('OPERATOR', 'OP_GTE', '>=', 4),
('OPERATOR', 'OP_LTE', '<=', 5),
('OPERATOR', 'OP_NEQ', '!=', 6);

-- 对比目标类型
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('COMPARE_TYPE', 'COMPARE_FIELD', '对比字段', 1),
('COMPARE_TYPE', 'COMPARE_NUMBER', '数值', 2),
('COMPARE_TYPE', 'COMPARE_TEXT', '文本', 3);

-- 数值单位
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('NUMBER_UNIT', 'UNIT_K', '千', 1),
('NUMBER_UNIT', 'UNIT_W', '万', 2),
('NUMBER_UNIT', 'UNIT_M', '百万', 3),
('NUMBER_UNIT', 'UNIT_B', '十亿', 4);

-- 币种
INSERT INTO auth_lookup (lookup_type, lookup_code, lookup_name, sort_order) VALUES
('CURRENCY', 'CNY', '人民币', 1),
('CURRENCY', 'USD', '美元', 2),
('CURRENCY', 'EUR', '欧元', 3);