-- V2__init_lookup_data.sql
-- Authorization Management System V7 - Lookup Data Initialization

-- 1. Lookup Types
INSERT INTO lookup_typess (type_code, type_name, description) VALUES
('AUTH_OBJECT_LEVEL', 'Authorization Object Level', 'Authorization object level list'),
('AUTH_PUBLISH_LEVEL', 'Authorization Publish Level', 'Authorization publish level list'),
('APPLICABLE_REGION', 'Applicable Region', 'Applicable region tree list'),
('AUTH_PUBLISH_ORG', 'Authorization Publish Organization', 'Authorization publish organization tree list'),
('INDUSTRY', 'Industry', 'Industry tree list'),
('BUSINESS_SCENE', 'Business Scene', 'Business scene list'),
('DECISION_LEVEL', 'Decision Level', 'Decision level list'),
('DOC_TYPE', 'Document Type', 'Document type list'),
('OPERATOR', 'Operator', 'Operator list'),
('COMPARE_UNIT', 'Compare Unit', 'Compare unit list'),
('LANGUAGE', 'Language', 'Language list'),
('DATA_TYPE', 'Data Type', 'Rule parameter data type'),
('STATUS', 'Status', 'Status list');

-- 2. Authorization Object Level Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'LEVEL1', 'Company', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'LEVEL2', 'BU', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'LEVEL3', 'Marketing Service', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'LEVEL4', 'Regional Department', 1, 4),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_OBJECT_LEVEL'), 'LEVEL5', 'Representative Office', 1, 5);

-- 3. Authorization Publish Level Values (same as object level)
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'LEVEL1', 'Company', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'LEVEL2', 'BU', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'LEVEL3', 'Marketing Service', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'LEVEL4', 'Regional Department', 1, 4),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_LEVEL'), 'LEVEL5', 'Representative Office', 1, 5);

-- 4. Applicable Region Tree Values (4-level tree)
INSERT INTO lookup_values (type_id, value_code, value_name, parent_code, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1', 'Headquarters', NULL, 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-1', 'Regional Department 1', '1', 2, 1),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-2', 'Regional Department 2', '1', 2, 2),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-1-1', 'Representative Office 1', '1-1', 3, 1),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-1-2', 'Representative Office 2', '1-1', 3, 2),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-2-1', 'Representative Office 3', '1-2', 3, 1),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-1-1-1', 'Office 1', '1-1-1', 4, 1),
((SELECT id FROM lookup_types WHERE type_code = 'APPLICABLE_REGION'), '1-1-1-2', 'Office 2', '1-1-1', 4, 2);

-- 5. Authorization Publish Organization Tree Values (same structure)
INSERT INTO lookup_values (type_id, value_code, value_name, parent_code, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1', 'Headquarters', NULL, 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-1', 'Regional Department 1', '1', 2, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-2', 'Regional Department 2', '1', 2, 2),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-1-1', 'Representative Office 1', '1-1', 3, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-1-2', 'Representative Office 2', '1-1', 3, 2),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-2-1', 'Representative Office 3', '1-2', 3, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-1-1-1', 'Office 1', '1-1-1', 4, 1),
((SELECT id FROM lookup_types WHERE type_code = 'AUTH_PUBLISH_ORG'), '1-1-1-2', 'Office 2', '1-1-1', 4, 2);

-- 6. Industry Tree Values
INSERT INTO lookup_values (type_id, value_code, value_name, parent_code, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'INDUSTRY'), 'LV1', 'Industry Level 1', NULL, 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'INDUSTRY'), 'LV1-LV2', 'Industry Level 2', 'LV1', 2, 1),
((SELECT id FROM lookup_types WHERE type_code = 'INDUSTRY'), 'LV1-LV2-LV3', 'Industry Level 3', 'LV1-LV2', 3, 1);

-- 7. Business Scene Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'BUSINESS_SCENE'), 'BS001', 'Sales Scene', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'BUSINESS_SCENE'), 'BS002', 'Purchase Scene', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'BUSINESS_SCENE'), 'BS003', 'Contract Scene', 1, 3);

-- 8. Decision Level Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'DECISION_LEVEL'), 'DL001', 'Decision Level 1', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'DECISION_LEVEL'), 'DL002', 'Decision Level 2', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'DECISION_LEVEL'), 'DL003', 'Decision Level 3', 1, 3);

-- 9. Document Type Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'DOC_TYPE'), 'PDF', 'PDF Document', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'DOC_TYPE'), 'WORD', 'Word Document', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'DOC_TYPE'), 'EXCEL', 'Excel Document', 1, 3);

-- 10. Operator Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'GT', '>', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'LT', '<', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'EQ', '=', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'GE', '>=', 1, 4),
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'LE', '<=', 1, 5),
((SELECT id FROM lookup_types WHERE type_code = 'OPERATOR'), 'NE', '!=', 1, 6);

-- 11. Compare Unit Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'COMPARE_UNIT'), 'CNY', 'CNY (Wan)', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'COMPARE_UNIT'), 'USD', 'USD (Wan)', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'COMPARE_UNIT'), 'WAN', 'Wan', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'COMPARE_UNIT'), 'ZHAO', 'Zhao', 1, 4);

-- 12. Language Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'LANGUAGE'), 'ZH', 'Chinese', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'LANGUAGE'), 'EN', 'English', 1, 2);

-- 13. Data Type Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'DATA_TYPE'), 'TEXT', 'Text', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'DATA_TYPE'), 'NUMBER', 'Number', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'DATA_TYPE'), 'FIELD', 'Compare Field', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'DATA_TYPE'), 'RATIO', 'Ratio', 1, 4);

-- 14. Status Values
INSERT INTO lookup_values (type_id, value_code, value_name, level, sort_order) VALUES
((SELECT id FROM lookup_types WHERE type_code = 'STATUS'), 'DRAFT', 'Draft', 1, 1),
((SELECT id FROM lookup_types WHERE type_code = 'STATUS'), 'PUBLISHED', 'Published', 1, 2),
((SELECT id FROM lookup_types WHERE type_code = 'STATUS'), 'INVALID', 'Invalid', 1, 3),
((SELECT id FROM lookup_types WHERE type_code = 'STATUS'), 'ACTIVE', 'Active', 1, 4),
((SELECT id FROM lookup_types WHERE type_code = 'STATUS'), 'INACTIVE', 'Inactive', 1, 5);