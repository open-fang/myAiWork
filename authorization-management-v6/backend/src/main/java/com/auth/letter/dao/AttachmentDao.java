package com.auth.letter.dao;

import com.auth.letter.entity.Attachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 附件DAO接口
 */
public interface AttachmentDao {

    /**
     * 查询附件列表
     */
    List<Attachment> selectList(@Param("authLetterId") Long authLetterId,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    /**
     * 查询附件总数
     */
    long countList(@Param("authLetterId") Long authLetterId);

    /**
     * 根据ID查询附件
     */
    Attachment selectById(@Param("id") Long id);

    /**
     * 插入附件
     */
    int insert(Attachment attachment);

    /**
     * 删除附件
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除附件
     */
    int deleteByIds(@Param("ids") List<Long> ids);
}