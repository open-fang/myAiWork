-- V1__init_schema.sql
-- Authorization Management System V7 - Database Schema Initialization

-- 1. Authorization Letter Table
CREATE TABLE auth_letters (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    auth_object_level   JSONB,
    applicable_region   JSONB,
    auth_publish_level  JSONB,
    auth_publish_org    JSONB,
    publish_year        INTEGER,
    summary             VARCHAR(4000),
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE UNIQUE INDEX uk_auth_letter_name ON auth_letters (name) WHERE delete_flag = 0;
CREATE INDEX idx_auth_letter_status ON auth_letters (status) WHERE delete_flag = 0;
CREATE INDEX idx_auth_letter_year ON auth_letters (publish_year) WHERE delete_flag = 0;
CREATE INDEX idx_auth_letter_object_level ON auth_letters USING GIN (auth_object_level);
CREATE INDEX idx_auth_letter_region ON auth_letters USING GIN (applicable_region);

-- 2. Authorization Letter Scene Table
CREATE TABLE auth_letter_scenes (
    id                  BIGSERIAL PRIMARY KEY,
    auth_letter_id      BIGINT NOT NULL,
    scene_name          VARCHAR(200) NOT NULL,
    industry            JSONB,
    business_scene      VARCHAR(50),
    decision_level      VARCHAR(50),
    specific_rule       VARCHAR(1000),
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_scene_auth_letter_id ON auth_letter_scenes (auth_letter_id) WHERE delete_flag = 0;
CREATE UNIQUE INDEX uk_scene_name ON auth_letter_scenes (auth_letter_id, scene_name) WHERE delete_flag = 0;
CREATE INDEX idx_scene_industry ON auth_letter_scenes USING GIN (industry);

-- 3. Authorization Letter Rule Table
CREATE TABLE auth_letter_rules (
    id                  BIGSERIAL PRIMARY KEY,
    scene_id            BIGINT NOT NULL,
    rule_name           VARCHAR(200),
    logic_type          VARCHAR(10) DEFAULT 'AND',
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_rule_scene_id ON auth_letter_rules (scene_id) WHERE delete_flag = 0;

-- 4. Authorization Letter Rule Condition Table
CREATE TABLE auth_letter_rule_conditions (
    id                      BIGSERIAL PRIMARY KEY,
    rule_id                 BIGINT NOT NULL,
    parent_condition_id     BIGINT,
    field_code              VARCHAR(50),
    operator                VARCHAR(10),
    compare_type            VARCHAR(20),
    compare_field_code      VARCHAR(50),
    compare_value           VARCHAR(500),
    compare_unit            VARCHAR(20),
    logic_type              VARCHAR(10) DEFAULT 'AND',
    is_group                SMALLINT DEFAULT 0,
    group_logic_type        VARCHAR(10),
    sort_order              INTEGER DEFAULT 0,
    created_by              VARCHAR(100),
    created_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(100),
    updated_time            TIMESTAMP,
    delete_flag             SMALLINT DEFAULT 0
);

CREATE INDEX idx_condition_rule_id ON auth_letter_rule_conditions (rule_id) WHERE delete_flag = 0;
CREATE INDEX idx_condition_parent_id ON auth_letter_rule_conditions (parent_condition_id) WHERE delete_flag = 0;

-- 5. Authorization Letter Attachment Table
CREATE TABLE auth_letter_attachments (
    id                  BIGSERIAL PRIMARY KEY,
    auth_letter_id      BIGINT NOT NULL,
    doc_id              VARCHAR(100) NOT NULL,
    doc_name            VARCHAR(200) NOT NULL,
    doc_type            VARCHAR(50),
    is_encrypted        SMALLINT DEFAULT 0,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_attachment_auth_letter_id ON auth_letter_attachments (auth_letter_id) WHERE delete_flag = 0;

-- 6. Authorization Letter Log Table
CREATE TABLE auth_letter_logs (
    id                  BIGSERIAL PRIMARY KEY,
    auth_letter_id      BIGINT NOT NULL,
    operation           VARCHAR(20) NOT NULL,
    operator            VARCHAR(100),
    operator_name       VARCHAR(200),
    operation_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_log_auth_letter_id ON auth_letter_logs (auth_letter_id);
CREATE INDEX idx_log_time ON auth_letter_logs (operation_time DESC);

-- 7. Rule Parameter Table
CREATE TABLE rule_params (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(100) NOT NULL,
    name_en                 VARCHAR(100) NOT NULL,
    business_objects        JSONB,
    status                  VARCHAR(20) DEFAULT 'ACTIVE',
    data_type               VARCHAR(20) NOT NULL,
    reference_field_id      BIGINT,
    created_by              VARCHAR(100),
    created_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(100),
    updated_time            TIMESTAMP,
    delete_flag             SMALLINT DEFAULT 0
);

CREATE UNIQUE INDEX uk_rule_param_name ON rule_params (name) WHERE delete_flag = 0;
CREATE UNIQUE INDEX uk_rule_param_name_en ON rule_params (name_en) WHERE delete_flag = 0;
CREATE INDEX idx_rule_param_status ON rule_params (status) WHERE delete_flag = 0;

-- 8. Questionnaire Question Table
CREATE TABLE questionnaire_questions (
    id                  BIGSERIAL PRIMARY KEY,
    question_code       VARCHAR(50) NOT NULL,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE UNIQUE INDEX uk_question_code ON questionnaire_questions (question_code);

-- 9. Questionnaire Question Text Table
CREATE TABLE questionnaire_question_texts (
    id                  BIGSERIAL PRIMARY KEY,
    question_id         BIGINT NOT NULL,
    question_text       VARCHAR(500) NOT NULL,
    language            VARCHAR(10) NOT NULL,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_question_text_question_id ON questionnaire_question_texts (question_id) WHERE delete_flag = 0;
CREATE UNIQUE INDEX uk_question_text_lang ON questionnaire_question_texts (question_id, language) WHERE delete_flag = 0;

-- 10. Questionnaire Answer Table
CREATE TABLE questionnaire_answers (
    id                  BIGSERIAL PRIMARY KEY,
    answer_code         VARCHAR(50) NOT NULL,
    question_id         BIGINT NOT NULL,
    sort_order          INTEGER DEFAULT 0,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE UNIQUE INDEX uk_answer_code ON questionnaire_answers (answer_code);
CREATE INDEX idx_answer_question_id ON questionnaire_answers (question_id) WHERE delete_flag = 0;

-- 11. Questionnaire Answer Text Table
CREATE TABLE questionnaire_answer_texts (
    id                  BIGSERIAL PRIMARY KEY,
    answer_id           BIGINT NOT NULL,
    answer_text         VARCHAR(200) NOT NULL,
    language            VARCHAR(10) NOT NULL,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_answer_text_answer_id ON questionnaire_answer_texts (answer_id) WHERE delete_flag = 0;
CREATE UNIQUE INDEX uk_answer_text_lang ON questionnaire_answer_texts (answer_id, language) WHERE delete_flag = 0;

-- 12. Scene Questionnaire Relation Table
CREATE TABLE scene_questionnaires (
    id                  BIGSERIAL PRIMARY KEY,
    scene_id            BIGINT NOT NULL,
    question_id         BIGINT NOT NULL,
    correct_answer_id   BIGINT,
    sort_order          INTEGER DEFAULT 0,
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_scene_q_scene_id ON scene_questionnaires (scene_id) WHERE delete_flag = 0;
CREATE INDEX idx_scene_q_question_id ON scene_questionnaires (question_id) WHERE delete_flag = 0;

-- 13. Lookup Type Table
CREATE TABLE lookup_types (
    id                  BIGSERIAL PRIMARY KEY,
    type_code           VARCHAR(50) NOT NULL,
    type_name           VARCHAR(100) NOT NULL,
    description         VARCHAR(500),
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE UNIQUE INDEX uk_lookup_type_code ON lookup_types (type_code) WHERE delete_flag = 0;

-- 14. Lookup Value Table
CREATE TABLE lookup_values (
    id                  BIGSERIAL PRIMARY KEY,
    type_id             BIGINT NOT NULL,
    value_code          VARCHAR(50) NOT NULL,
    value_name          VARCHAR(200) NOT NULL,
    parent_code         VARCHAR(50),
    level               INTEGER DEFAULT 1,
    sort_order          INTEGER DEFAULT 0,
    status              VARCHAR(20) DEFAULT 'ACTIVE',
    created_by          VARCHAR(100),
    created_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(100),
    updated_time        TIMESTAMP,
    delete_flag         SMALLINT DEFAULT 0
);

CREATE INDEX idx_lookup_value_type_id ON lookup_values (type_id) WHERE delete_flag = 0;
CREATE INDEX idx_lookup_value_parent_code ON lookup_values (parent_code) WHERE delete_flag = 0;
CREATE INDEX idx_lookup_value_status ON lookup_values (status) WHERE delete_flag = 0;