package com.auth.letter.mapper;

import com.auth.letter.entity.RuleParam;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RuleParamMapper {

    @Select("<script>" +
            "SELECT * FROM rule_param WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND LOWER(name) LIKE CONCAT('%', LOWER(#{name}), '%') </if>" +
            "<if test='nameEn != null and nameEn != \"\"'> AND LOWER(name_en) LIKE CONCAT('%', LOWER(#{nameEn}), '%') </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<RuleParam> selectByCondition(@Param("name") String name,
                                       @Param("nameEn") String nameEn,
                                       @Param("status") String status,
                                       @Param("pageSize") int pageSize,
                                       @Param("offset") int offset);

    @Select("<script>" +
            "SELECT COUNT(*) FROM rule_param WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND LOWER(name) LIKE CONCAT('%', LOWER(#{name}), '%') </if>" +
            "<if test='nameEn != null and nameEn != \"\"'> AND LOWER(name_en) LIKE CONCAT('%', LOWER(#{nameEn}), '%') </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "</script>")
    long countByCondition(@Param("name") String name,
                          @Param("nameEn") String nameEn,
                          @Param("status") String status);

    @Select("SELECT * FROM rule_param WHERE id = #{id}")
    RuleParam selectById(@Param("id") Long id);

    @Select("SELECT * FROM rule_param WHERE status = 'ACTIVE' ORDER BY name")
    List<RuleParam> selectAllActive();

    @Insert("INSERT INTO rule_param (name, name_en, status, data_type, business_mappings, created_by, created_at) " +
            "VALUES (#{name}, #{nameEn}, #{status}, #{dataType}, #{businessMappings}, #{createdBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RuleParam ruleParam);

    @Update("UPDATE rule_param SET name = #{name}, name_en = #{nameEn}, status = #{status}, " +
            "data_type = #{dataType}, business_mappings = #{businessMappings}, " +
            "updated_by = #{updatedBy}, updated_at = NOW() WHERE id = #{id}")
    int update(RuleParam ruleParam);

    @Update("<script>" +
            "UPDATE rule_param SET status = 'ACTIVE', updated_at = NOW() " +
            "WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchActivate(@Param("ids") List<Long> ids);

    @Update("<script>" +
            "UPDATE rule_param SET status = 'INACTIVE', updated_at = NOW() " +
            "WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchDeactivate(@Param("ids") List<Long> ids);
}