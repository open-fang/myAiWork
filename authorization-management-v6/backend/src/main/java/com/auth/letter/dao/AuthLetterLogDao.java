package com.auth.letter.dao;

import com.auth.letter.entity.AuthLetterLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志DAO接口
 */
public interface AuthLetterLogDao {

    /**
     * 查询日志列表
     */
    List<AuthLetterLog> selectList(@Param("authLetterId") Long authLetterId,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    /**
     * 查询日志总数
     */
    long countList(@Param("authLetterId") Long authLetterId);

    /**
     * 插入日志
     */
    int insert(AuthLetterLog log);
}