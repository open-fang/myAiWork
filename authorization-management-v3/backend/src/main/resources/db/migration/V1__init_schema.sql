-- V1__init_schema.sql
-- Authorization Letter Management System V3

-- Authorization Letter Table
CREATE TABLE auth_letter (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    publish_level VARCHAR(30),
    authorized_level VARCHAR(30),
    auth_target_level VARCHAR(500),
    applicable_region VARCHAR(500),
    auth_publish_level VARCHAR(500),
    auth_publish_org VARCHAR(1000),
    publish_year INTEGER,
    content_summary TEXT,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);

-- Scene Table
CREATE TABLE scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    decision_level INTEGER,
    order_index INTEGER DEFAULT 0,
    industry VARCHAR(500),
    business_scenario VARCHAR(100),
    decision_level_code VARCHAR(50),
    rule_detail TEXT,
    conditions JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scene_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

CREATE INDEX idx_scene_auth_letter_id ON scene(auth_letter_id);

-- Rule Table
CREATE TABLE rule (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    order_index INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rule_scene FOREIGN KEY (scene_id) REFERENCES scene(id) ON DELETE CASCADE
);

CREATE INDEX idx_rule_scene_id ON rule(scene_id);

-- Condition Table
CREATE TABLE condition (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT,
    parent_id BIGINT,
    logic_operator VARCHAR(10),
    field_name VARCHAR(100),
    operator VARCHAR(20),
    value VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_condition_rule FOREIGN KEY (rule_id) REFERENCES rule(id) ON DELETE CASCADE,
    CONSTRAINT fk_condition_parent FOREIGN KEY (parent_id) REFERENCES condition(id) ON DELETE CASCADE
);

CREATE INDEX idx_condition_rule_id ON condition(rule_id);
CREATE INDEX idx_condition_parent_id ON condition(parent_id);

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

CREATE INDEX idx_rule_param_status ON rule_param(status);
CREATE INDEX idx_rule_param_name ON rule_param(name);