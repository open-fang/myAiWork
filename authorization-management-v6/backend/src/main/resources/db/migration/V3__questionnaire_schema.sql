-- V3__questionnaire_schema.sql
-- 问卷题目配置功能数据库表结构

-- 1. 问卷题目主表
CREATE TABLE IF NOT EXISTS questionnaire_question (
    id BIGSERIAL PRIMARY KEY,
    question_code VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE questionnaire_question IS '问卷题目主表';
COMMENT ON COLUMN questionnaire_question.id IS '主键ID';
COMMENT ON COLUMN questionnaire_question.question_code IS '题目编号，格式：QT+时间戳';
COMMENT ON COLUMN questionnaire_question.status IS '状态：ACTIVE-生效，INACTIVE-失效';

CREATE INDEX idx_question_code ON questionnaire_question(question_code);
CREATE INDEX idx_question_status ON questionnaire_question(status);
CREATE INDEX idx_question_created_time ON questionnaire_question(created_time);

-- 2. 问卷题目文本表（多语言）
CREATE TABLE IF NOT EXISTS questionnaire_question_text (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    language VARCHAR(10) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_question_language ON questionnaire_question_text(question_id, language) WHERE delete_flag = 0;

COMMENT ON TABLE questionnaire_question_text IS '问卷题目文本表（多语言）';
COMMENT ON COLUMN questionnaire_question_text.id IS '主键ID';
COMMENT ON COLUMN questionnaire_question_text.question_id IS '题目ID';
COMMENT ON COLUMN questionnaire_question_text.question_text IS '题目内容';
COMMENT ON COLUMN questionnaire_question_text.language IS '语言代码：ZH-中文，EN-英文';

CREATE INDEX idx_question_text_question_id ON questionnaire_question_text(question_id);
CREATE INDEX idx_question_text_language ON questionnaire_question_text(language);

-- 3. 问卷答案主表
CREATE TABLE IF NOT EXISTS questionnaire_answer (
    id BIGSERIAL PRIMARY KEY,
    answer_code VARCHAR(50) NOT NULL UNIQUE,
    question_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE questionnaire_answer IS '问卷答案主表';
COMMENT ON COLUMN questionnaire_answer.id IS '主键ID';
COMMENT ON COLUMN questionnaire_answer.answer_code IS '答案编号，格式：ANS+时间戳';
COMMENT ON COLUMN questionnaire_answer.question_id IS '关联题目ID';
COMMENT ON COLUMN questionnaire_answer.sort_order IS '排序序号';

CREATE INDEX idx_answer_code ON questionnaire_answer(answer_code);
CREATE INDEX idx_answer_question_id ON questionnaire_answer(question_id);
CREATE INDEX idx_answer_sort_order ON questionnaire_answer(sort_order);

-- 4. 问卷答案文本表（多语言）
CREATE TABLE IF NOT EXISTS questionnaire_answer_text (
    id BIGSERIAL PRIMARY KEY,
    answer_id BIGINT NOT NULL,
    answer_text VARCHAR(200) NOT NULL,
    language VARCHAR(10) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64),
    updated_time TIMESTAMP,
    delete_flag SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_answer_language ON questionnaire_answer_text(answer_id, language) WHERE delete_flag = 0;

COMMENT ON TABLE questionnaire_answer_text IS '问卷答案文本表（多语言）';
COMMENT ON COLUMN questionnaire_answer_text.id IS '主键ID';
COMMENT ON COLUMN questionnaire_answer_text.answer_id IS '答案ID';
COMMENT ON COLUMN questionnaire_answer_text.answer_text IS '答案内容';
COMMENT ON COLUMN questionnaire_answer_text.language IS '语言代码：ZH-中文，EN-英文';

CREATE INDEX idx_answer_text_answer_id ON questionnaire_answer_text(answer_id);
CREATE INDEX idx_answer_text_language ON questionnaire_answer_text(language);

-- 5. 初始化语言Lookup数据
INSERT INTO lookup_type (type_code, type_name, description, created_by)
SELECT 'QUESTIONNAIRE_LANGUAGE', '问卷语言', '问卷内容语言类型', 'system'
WHERE NOT EXISTS (SELECT 1 FROM lookup_type WHERE type_code = 'QUESTIONNAIRE_LANGUAGE');

INSERT INTO lookup_value (type_id, value_code, value_name, sort_order, status, created_by)
SELECT lt.id, 'ZH', '中文', 1, 'ACTIVE', 'system'
FROM lookup_type lt
WHERE lt.type_code = 'QUESTIONNAIRE_LANGUAGE'
AND NOT EXISTS (
    SELECT 1 FROM lookup_value lv
    WHERE lv.type_id = lt.id AND lv.value_code = 'ZH'
);

INSERT INTO lookup_value (type_id, value_code, value_name, sort_order, status, created_by)
SELECT lt.id, 'EN', 'English', 2, 'ACTIVE', 'system'
FROM lookup_type lt
WHERE lt.type_code = 'QUESTIONNAIRE_LANGUAGE'
AND NOT EXISTS (
    SELECT 1 FROM lookup_value lv
    WHERE lv.type_id = lt.id AND lv.value_code = 'EN'
);