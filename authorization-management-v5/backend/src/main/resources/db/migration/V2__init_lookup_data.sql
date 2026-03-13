-- =====================================================
-- 授权书管理系统 V5 初始数据脚本
-- =====================================================

-- 1. 插入授权对象层级数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('AUTH_TARGET_LEVEL', 'LEVEL_1', '一级', 1),
('AUTH_TARGET_LEVEL', 'LEVEL_2', '二级', 2),
('AUTH_TARGET_LEVEL', 'LEVEL_3', '三级', 3);

-- 2. 插入适用区域数据（三层树形：机关、地区部、代表处）
-- 机关层级
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order) VALUES
('APPLICABLE_REGION', 'ORG_HQ', '机关', NULL, 1);

-- 地区部层级
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REGION_EAST', '华东地区部', id, 1 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REGION_SOUTH', '华南地区部', id, 2 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REGION_NORTH', '华北地区部', id, 3 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REGION_WEST', '西部地区部', id, 4 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'ORG_HQ';

-- 代表处层级 - 华东地区部下属
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_SH', '上海代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_EAST';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_HZ', '杭州代表处', id, 2 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_EAST';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_NJ', '南京代表处', id, 3 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_EAST';

-- 代表处层级 - 华南地区部下属
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_GZ', '广州代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_SOUTH';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_SZ', '深圳代表处', id, 2 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_SOUTH';

-- 代表处层级 - 华北地区部下属
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_BJ', '北京代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_NORTH';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_TJ', '天津代表处', id, 2 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_NORTH';

-- 代表处层级 - 西部地区部下属
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_CD', '成都代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_WEST';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'APPLICABLE_REGION', 'REP_XA', '西安代表处', id, 2 FROM auth_lookup_value WHERE type_code = 'APPLICABLE_REGION' AND item_value = 'REGION_WEST';

-- 3. 插入授权发布层级数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_1', '一级发布', 1),
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_2', '二级发布', 2),
('AUTH_PUBLISH_LEVEL', 'PUBLISH_LEVEL_3', '三级发布', 3);

-- 4. 插入授权发布组织数据（三层树形：机关、地区部、代表处）
-- 机关层级
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order) VALUES
('AUTH_PUBLISH_ORG', 'PUB_ORG_HQ', '机关', NULL, 1);

-- 地区部层级
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REGION_EAST', '华东地区部', id, 1 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REGION_SOUTH', '华南地区部', id, 2 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REGION_NORTH', '华北地区部', id, 3 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_ORG_HQ';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REGION_WEST', '西部地区部', id, 4 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_ORG_HQ';

-- 代表处层级
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REP_SH', '上海代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_REGION_EAST';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REP_GZ', '广州代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_REGION_SOUTH';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REP_BJ', '北京代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_REGION_NORTH';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'AUTH_PUBLISH_ORG', 'PUB_REP_CD', '成都代表处', id, 1 FROM auth_lookup_value WHERE type_code = 'AUTH_PUBLISH_ORG' AND item_value = 'PUB_REGION_WEST';

-- 5. 插入产业数据（树形结构）
-- 一级产业
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order) VALUES
('INDUSTRY', 'IND_TECH', '科技产业', NULL, 1),
('INDUSTRY', 'IND_FIN', '金融产业', NULL, 2),
('INDUSTRY', 'IND_MANU', '制造产业', NULL, 3);

-- 二级产业 - 科技产业
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_TECH_SW', '软件', id, 1 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_TECH';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_TECH_HW', '硬件', id, 2 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_TECH';

-- 二级产业 - 金融产业
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_FIN_BANK', '银行', id, 1 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_FIN';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_FIN_INS', '保险', id, 2 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_FIN';

-- 二级产业 - 制造产业
INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_MANU_AUTO', '汽车', id, 1 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_MANU';

INSERT INTO auth_lookup_value (type_code, item_value, label, parent_id, sort_order)
SELECT 'INDUSTRY', 'IND_MANU_ELEC', '电子', id, 2 FROM auth_lookup_value WHERE type_code = 'INDUSTRY' AND item_value = 'IND_MANU';

-- 6. 插入业务场景数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('BUSINESS_SCENE', 'SCENE_CONTRACT', '合同签订', 1),
('BUSINESS_SCENE', 'SCENE_PAYMENT', '付款审批', 2),
('BUSINESS_SCENE', 'SCENE_PURCHASE', '采购审批', 3),
('BUSINESS_SCENE', 'SCENE_INVEST', '投资决策', 4),
('BUSINESS_SCENE', 'SCENE_PERSONNEL', '人事任免', 5);

-- 7. 插入决策层级数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('DECISION_LEVEL', 'DEC_BOARD', '董事会', 1),
('DECISION_LEVEL', 'DEC_EXEC', '执行层', 2),
('DECISION_LEVEL', 'DEC_MGMT', '管理层', 3),
('DECISION_LEVEL', 'DEC_DEPT', '部门级', 4);

-- 8. 插入文档类型数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('DOC_TYPE', 'DOC_PDF', 'PDF文档', 1),
('DOC_TYPE', 'DOC_WORD', 'Word文档', 2),
('DOC_TYPE', 'DOC_EXCEL', 'Excel文档', 3),
('DOC_TYPE', 'DOC_IMAGE', '图片', 4),
('DOC_TYPE', 'DOC_OTHER', '其他', 5);

-- 9. 插入计量单位数据（千万兆）
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('MEASURE_UNIT', 'UNIT_YI', '亿', 1),
('MEASURE_UNIT', 'UNIT_QIANWAN', '千万', 2),
('MEASURE_UNIT', 'UNIT_BAIWAN', '百万', 3),
('MEASURE_UNIT', 'UNIT_WAN', '万', 4),
('MEASURE_UNIT', 'UNIT_QIAN', '千', 5);

-- 10. 插入币种数据
INSERT INTO auth_lookup_value (type_code, item_value, label, sort_order) VALUES
('CURRENCY', 'CNY', '人民币', 1),
('CURRENCY', 'USD', '美元', 2),
('CURRENCY', 'EUR', '欧元', 3),
('CURRENCY', 'JPY', '日元', 4),
('CURRENCY', 'GBP', '英镑', 5);