package com.auth.letter.mapper;

import com.auth.letter.entity.AuthLetter;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AuthLetterMapper {

    @Select("<script>" +
            "SELECT * FROM auth_letter WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND LOWER(name) LIKE CONCAT('%', LOWER(#{name}), '%') </if>" +
            "<if test='publishYear != null'> AND publish_year = #{publishYear} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<AuthLetter> selectByCondition(@Param("name") String name,
                                        @Param("publishYear") Integer publishYear,
                                        @Param("status") String status,
                                        @Param("pageSize") int pageSize,
                                        @Param("offset") int offset);

    @Select("<script>" +
            "SELECT COUNT(*) FROM auth_letter WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND LOWER(name) LIKE CONCAT('%', LOWER(#{name}), '%') </if>" +
            "<if test='publishYear != null'> AND publish_year = #{publishYear} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "</script>")
    long countByCondition(@Param("name") String name,
                          @Param("publishYear") Integer publishYear,
                          @Param("status") String status);

    @Select("SELECT * FROM auth_letter WHERE id = #{id}")
    AuthLetter selectById(@Param("id") Long id);

    @Insert("INSERT INTO auth_letter (code, name, status, auth_target_level, applicable_region, " +
            "auth_publish_level, auth_publish_org, publish_year, content_summary, created_by, created_at) " +
            "VALUES (#{code}, #{name}, #{status}, #{authTargetLevel}, #{applicableRegion}, " +
            "#{authPublishLevel}, #{authPublishOrg}, #{publishYear}, #{contentSummary}, #{createdBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuthLetter authLetter);

    @Update("UPDATE auth_letter SET name = #{name}, auth_target_level = #{authTargetLevel}, " +
            "applicable_region = #{applicableRegion}, auth_publish_level = #{authPublishLevel}, " +
            "auth_publish_org = #{authPublishOrg}, publish_year = #{publishYear}, " +
            "content_summary = #{contentSummary}, updated_by = #{updatedBy}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(AuthLetter authLetter);

    @Update("UPDATE auth_letter SET status = 'PUBLISHED', published_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int publish(@Param("id") Long id);

    @Update("UPDATE auth_letter SET status = 'EXPIRED', updated_at = NOW() WHERE id = #{id}")
    int expire(@Param("id") Long id);

    @Delete("DELETE FROM auth_letter WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("<script>" +
            "UPDATE auth_letter SET status = 'PUBLISHED', published_at = NOW(), updated_at = NOW() " +
            "WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND status = 'DRAFT'" +
            "</script>")
    int batchPublish(@Param("ids") List<Long> ids);

    @Update("<script>" +
            "UPDATE auth_letter SET status = 'EXPIRED', updated_at = NOW() " +
            "WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND status = 'PUBLISHED'" +
            "</script>")
    int batchExpire(@Param("ids") List<Long> ids);

    @Delete("<script>" +
            "DELETE FROM auth_letter WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND status IN ('DRAFT', 'EXPIRED')" +
            "</script>")
    int batchDelete(@Param("ids") List<Long> ids);
}