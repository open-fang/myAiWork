package com.auth.letter.common;

/**
 * 常量定义
 */
public class Constants {

    /**
     * 授权书状态
     */
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_INVALID = "INVALID";

    /**
     * 规则参数状态
     */
    public static final String RULE_PARAM_ACTIVE = "ACTIVE";
    public static final String RULE_PARAM_INACTIVE = "INACTIVE";

    /**
     * 下拉值状态
     */
    public static final String LOOKUP_ACTIVE = "ACTIVE";
    public static final String LOOKUP_INACTIVE = "INACTIVE";

    /**
     * 逻辑类型
     */
    public static final String LOGIC_AND = "AND";
    public static final String LOGIC_OR = "OR";

    /**
     * 对比类型
     */
    public static final String COMPARE_TYPE_FIELD = "FIELD";
    public static final String COMPARE_TYPE_NUMBER = "NUMBER";
    public static final String COMPARE_TYPE_TEXT = "TEXT";
    public static final String COMPARE_TYPE_RATIO = "RATIO";

    /**
     * 数据类型
     */
    public static final String DATA_TYPE_TEXT = "TEXT";
    public static final String DATA_TYPE_NUMBER = "NUMBER";
    public static final String DATA_TYPE_FIELD = "FIELD";
    public static final String DATA_TYPE_RATIO = "RATIO";

    /**
     * 运算符
     */
    public static final String OPERATOR_GT = ">";
    public static final String OPERATOR_LT = "<";
    public static final String OPERATOR_EQ = "=";
    public static final String OPERATOR_GE = ">=";
    public static final String OPERATOR_LE = "<=";
    public static final String OPERATOR_NE = "!=";

    /**
     * 操作类型
     */
    public static final String OPERATION_CREATE = "CREATE";
    public static final String OPERATION_UPDATE = "UPDATE";
    public static final String OPERATION_PUBLISH = "PUBLISH";
    public static final String OPERATION_INVALIDATE = "INVALIDATE";
    public static final String OPERATION_DELETE = "DELETE";

    /**
     * 删除标识
     */
    public static final int DELETE_FLAG_NO = 0;
    public static final int DELETE_FLAG_YES = 1;

    private Constants() {
    }
}