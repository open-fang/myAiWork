-- V2__init_lookup_data.sql
-- 初始化下拉列表数据

-- 1. 插入下拉类型
INSERT INTO lookup_type (type_code, type_name, description, created_by) VALUES
('AUTH_OBJECT_LEVEL', '授权对象层级', '授权对象层级选择', 'system'),
('AUTH_PUBLISH_LEVEL', '授权发布层级', '授权发布层级选择', 'system'),
('APPLICABLE_REGION', '适用区域', '适用区域选择，三层树形结构', 'system'),
('AUTH_PUBLISH_ORG', '授权发布组织', '授权发布组织选择，三层树形结构', 'system'),
('INDUSTRY', '产业', '产业选择，树形结构', 'system'),
('BUSINESS_SCENE', '业务场景', '业务场景选择', 'system'),
('DECISION_LEVEL', '决策层级', '决策层级选择', 'system'),
('DOC_TYPE', '文档类型', '文档类型选择', 'system'),
('COMPARE_UNIT', '计量单位', '计量单位选择', 'system'),
('OPERATOR', '运算符', '运算符选择', 'system');

-- 2. 插入授权对象层级
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'L1', '一级层级', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'L2', '二级层级', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'L3', '三级层级', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'L4', '四级层级', 1, 4, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'L5', '五级层级', 1, 5, 'system');

-- 3. 插入授权发布层级
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'PL1', '发布层级一', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'PL2', '发布层级二', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'PL3', '发布层级三', 1, 3, 'system');

-- 4. 插入适用区域（三层树形：机关、地区部、代表处）
-- 第一层：机关
INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'ORG_HQ', '机关', NULL, 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'ORG_REGION', '地区部', NULL, 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'ORG_REP', '代表处', NULL, 1, 3, 'system');

-- 第二层：地区部下属
INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REGION_EAST', '华东地区部', 'ORG_REGION', 2, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REGION_SOUTH', '华南地区部', 'ORG_REGION', 2, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REGION_NORTH', '华北地区部', 'ORG_REGION', 2, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REGION_WEST', '西部地区部', 'ORG_REGION', 2, 4, 'system');

-- 第三层：代表处下属
INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_SH', '上海代表处', 'REGION_EAST', 3, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_NJ', '南京代表处', 'REGION_EAST', 3, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_GZ', '广州代表处', 'REGION_SOUTH', 3, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_SZ', '深圳代表处', 'REGION_SOUTH', 3, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_BJ', '北京代表处', 'REGION_NORTH', 3, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'APPLICABLE_REGION'), 'REP_CD', '成都代表处', 'REGION_WEST', 3, 1, 'system');

-- 5. 插入授权发布组织（同适用区域结构）
INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_HQ', '机关', NULL, 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REGION', '地区部', NULL, 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REP', '代表处', NULL, 1, 3, 'system');

INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REGION_EAST', '华东地区部', 'PUB_REGION', 2, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REGION_SOUTH', '华南地区部', 'PUB_REGION', 2, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REGION_NORTH', '华北地区部', 'PUB_REGION', 2, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REGION_WEST', '西部地区部', 'PUB_REGION', 2, 4, 'system');

INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REP_SH', '上海代表处', 'PUB_REGION_EAST', 3, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REP_NJ', '南京代表处', 'PUB_REGION_EAST', 3, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REP_GZ', '广州代表处', 'PUB_REGION_SOUTH', 3, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'AUTH_PUBLISH_ORG'), 'PUB_REP_SZ', '深圳代表处', 'PUB_REGION_SOUTH', 3, 2, 'system');

-- 6. 插入产业（树形结构）
INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_TECH', '科技产业', NULL, 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_FIN', '金融产业', NULL, 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_MFG', '制造产业', NULL, 1, 3, 'system');

INSERT INTO lookup_value (type_id, value_code, value_name, parent_code, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_TECH_SW', '软件', 'IND_TECH', 2, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_TECH_HW', '硬件', 'IND_TECH', 2, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_FIN_BANK', '银行', 'IND_FIN', 2, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_FIN_SEC', '证券', 'IND_FIN', 2, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_MFG_AUTO', '汽车制造', 'IND_MFG', 2, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'INDUSTRY'), 'IND_MFG_ELEC', '电子制造', 'IND_MFG', 2, 2, 'system');

-- 7. 插入业务场景
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'BUSINESS_SCENE'), 'BS_CONTRACT', '合同审批', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'BUSINESS_SCENE'), 'BS_PURCHASE', '采购审批', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'BUSINESS_SCENE'), 'BS_PAYMENT', '付款审批', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'BUSINESS_SCENE'), 'BS_PROJECT', '项目审批', 1, 4, 'system');

-- 8. 插入决策层级
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'DECISION_LEVEL'), 'DL1', '一级决策', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DECISION_LEVEL'), 'DL2', '二级决策', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DECISION_LEVEL'), 'DL3', '三级决策', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DECISION_LEVEL'), 'DL4', '四级决策', 1, 4, 'system');

-- 9. 插入文档类型
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'DOC_TYPE'), 'PDF', 'PDF文档', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DOC_TYPE'), 'WORD', 'Word文档', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DOC_TYPE'), 'EXCEL', 'Excel文档', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DOC_TYPE'), 'IMAGE', '图片', 1, 4, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'DOC_TYPE'), 'OTHER', '其他', 1, 5, 'system');

-- 10. 插入计量单位
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'CNY', '人民币(元)', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'USD', '美元', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'EUR', '欧元', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'WAN', '万', 1, 4, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'YI', '亿', 1, 5, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'COMPARE_UNIT'), 'PERCENT', '百分比(%)', 1, 6, 'system');

-- 11. 插入运算符
INSERT INTO lookup_value (type_id, value_code, value_name, level, sort_order, created_by) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'GT', '大于(>)', 1, 1, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'LT', '小于(<)', 1, 2, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'EQ', '等于(=)', 1, 3, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'GE', '大于等于(>=)', 1, 4, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'LE', '小于等于(<=)', 1, 5, 'system'),
((SELECT id FROM lookup_type WHERE type_code = 'OPERATOR'), 'NE', '不等于(!=)', 1, 6, 'system');

-- 12. 初始化规则参数数据
INSERT INTO rule_param (name, name_en, business_object, value_logic, status, data_type, created_by) VALUES
('金额', 'amount', '订单', '$.order.amount', 'ACTIVE', 'NUMBER', 'system'),
('币种', 'currency', '订单', '$.order.currency', 'ACTIVE', 'TEXT', 'system'),
('地区', 'region', '订单', '$.order.region', 'ACTIVE', 'TEXT', 'system'),
('产业', 'industry', '订单', '$.order.industry', 'ACTIVE', 'TEXT', 'system'),
('客户等级', 'customerLevel', '客户', '$.customer.level', 'ACTIVE', 'TEXT', 'system'),
('合同金额', 'contractAmount', '合同', '$.contract.amount', 'ACTIVE', 'NUMBER', 'system'),
('合同类型', 'contractType', '合同', '$.contract.type', 'ACTIVE', 'TEXT', 'system'),
('项目金额', 'projectAmount', '项目', '$.project.amount', 'ACTIVE', 'NUMBER', 'system'),
('审批比例', 'approvalRatio', '审批', '$.approval.ratio', 'ACTIVE', 'RATIO', 'system');