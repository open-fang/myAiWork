package com.auth.letter.mapper;

import com.auth.letter.entity.AuthScene;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AuthSceneMapper {

    @Select("SELECT * FROM auth_scene WHERE auth_letter_id = #{authLetterId} ORDER BY order_index, created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<AuthScene> selectByAuthLetterId(@Param("authLetterId") Long authLetterId,
                                          @Param("pageSize") int pageSize,
                                          @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM auth_scene WHERE auth_letter_id = #{authLetterId}")
    long countByAuthLetterId(@Param("authLetterId") Long authLetterId);

    @Select("SELECT * FROM auth_scene WHERE id = #{id}")
    AuthScene selectById(@Param("id") Long id);

    @Insert("INSERT INTO auth_scene (auth_letter_id, scene_name, industry, business_scenario, decision_level, " +
            "rule_detail, condition_groups, order_index, created_by, created_at) " +
            "VALUES (#{authLetterId}, #{sceneName}, #{industry}, #{businessScenario}, #{decisionLevel}, " +
            "#{ruleDetail}, #{conditionGroups}, #{orderIndex}, #{createdBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuthScene scene);

    @Update("UPDATE auth_scene SET scene_name = #{sceneName}, industry = #{industry}, " +
            "business_scenario = #{businessScenario}, decision_level = #{decisionLevel}, " +
            "rule_detail = #{ruleDetail}, condition_groups = #{conditionGroups}, " +
            "updated_by = #{updatedBy}, updated_at = NOW() WHERE id = #{id}")
    int update(AuthScene scene);

    @Delete("DELETE FROM auth_scene WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("<script>" +
            "DELETE FROM auth_scene WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchDelete(@Param("ids") List<Long> ids);

    @Delete("DELETE FROM auth_scene WHERE auth_letter_id = #{authLetterId}")
    int deleteByAuthLetterId(@Param("authLetterId") Long authLetterId);
}