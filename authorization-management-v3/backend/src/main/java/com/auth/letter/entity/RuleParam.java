package com.auth.letter.entity;

import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 规则参数实体
 */
@Data
@Entity
@Table(name = "rule_param")
public class RuleParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 名称英文
     */
    @Column(nullable = false, length = 100, unique = true)
    private String nameEn;

    /**
     * 状态: ACTIVE-生效, INACTIVE-失效
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 数据类型: TEXT-文本, NUMBER-数值, COMPARE_FIELD-比对字段, RATIO-比率
     */
    @Column(nullable = false, length = 30)
    private String dataType;

    /**
     * 业务对象与取值逻辑映射（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String businessMappings;

    /**
     * 创建人
     */
    @Column(length = 50)
    private String createdBy;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    @Column(length = 50)
    private String updatedBy;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}