-- 删除不再需要的唯一索引
-- 新设计允许同一答案下多个答案条目使用相同语言，因此(answer_id, language)组合不再需要唯一约束

DROP INDEX IF EXISTS uk_answer_language;