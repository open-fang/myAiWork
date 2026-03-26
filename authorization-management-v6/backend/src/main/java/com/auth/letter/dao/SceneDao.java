package com.auth.letter.dao;

import com.auth.letter.entity.Scene;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场景DAO接口
 */
public interface SceneDao {

    /**
     * 查询场景列表
     */
    List<Scene> selectList(@Param("authLetterId") Long authLetterId,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    /**
     * 查询场景总数
     */
    long countList(@Param("authLetterId") Long authLetterId);

    /**
     * 根据ID查询场景
     */
    Scene selectById(@Param("id") Long id);

    /**
     * 根据授权书ID查询所有场景
     */
    List<Scene> selectByAuthLetterId(@Param("authLetterId") Long authLetterId);

    /**
     * 插入场景
     */
    int insert(Scene scene);

    /**
     * 更新场景
     */
    int update(Scene scene);

    /**
     * 删除场景
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除场景
     */
    int deleteByIds(@Param("ids") List<Long> ids);
}