package com.auth.letter.dao;

import com.auth.letter.entity.AuthLetter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 授权书DAO接口
 */
public interface AuthLetterDao {

    /**
     * 查询授权书列表
     */
    List<AuthLetter> selectList(@Param("name") String name,
                                 @Param("authObjectLevel") String authObjectLevel,
                                 @Param("applicableRegion") String applicableRegion,
                                 @Param("authPublishLevel") String authPublishLevel,
                                 @Param("authPublishOrg") String authPublishOrg,
                                 @Param("publishYear") Integer publishYear,
                                 @Param("status") String status,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    /**
     * 查询授权书总数
     */
    long countList(@Param("name") String name,
                   @Param("authObjectLevel") String authObjectLevel,
                   @Param("applicableRegion") String applicableRegion,
                   @Param("authPublishLevel") String authPublishLevel,
                   @Param("authPublishOrg") String authPublishOrg,
                   @Param("publishYear") Integer publishYear,
                   @Param("status") String status);

    /**
     * 根据ID查询授权书
     */
    AuthLetter selectById(@Param("id") Long id);

    /**
     * 插入授权书
     */
    int insert(AuthLetter authLetter);

    /**
     * 更新授权书
     */
    int update(AuthLetter authLetter);

    /**
     * 删除授权书（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除授权书
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 更新授权书状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updatedBy") String updatedBy);
}