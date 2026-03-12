package com.auth.letter.mapper;

import com.auth.letter.entity.AuthAttachment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AuthAttachmentMapper {

    @Select("SELECT * FROM auth_attachment WHERE auth_letter_id = #{authLetterId} ORDER BY created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<AuthAttachment> selectByAuthLetterId(@Param("authLetterId") Long authLetterId,
                                               @Param("pageSize") int pageSize,
                                               @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM auth_attachment WHERE auth_letter_id = #{authLetterId}")
    long countByAuthLetterId(@Param("authLetterId") Long authLetterId);

    @Select("SELECT * FROM auth_attachment WHERE id = #{id}")
    AuthAttachment selectById(@Param("id") Long id);

    @Insert("INSERT INTO auth_attachment (auth_letter_id, doc_id, doc_name, doc_type, is_encrypted, created_by, created_at) " +
            "VALUES (#{authLetterId}, #{docId}, #{docName}, #{docType}, #{isEncrypted}, #{createdBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuthAttachment attachment);

    @Delete("DELETE FROM auth_attachment WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("<script>" +
            "DELETE FROM auth_attachment WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchDelete(@Param("ids") List<Long> ids);
}