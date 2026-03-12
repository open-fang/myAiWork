package com.auth.letter.mapper;

import com.auth.letter.entity.AuthOperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AuthOperationLogMapper {

    @Select("SELECT * FROM auth_operation_log WHERE auth_letter_id = #{authLetterId} ORDER BY operated_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<AuthOperationLog> selectByAuthLetterId(@Param("authLetterId") Long authLetterId,
                                                  @Param("pageSize") int pageSize,
                                                  @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM auth_operation_log WHERE auth_letter_id = #{authLetterId}")
    long countByAuthLetterId(@Param("authLetterId") Long authLetterId);

    @Insert("INSERT INTO auth_operation_log (auth_letter_id, operation, operator, operated_at) " +
            "VALUES (#{authLetterId}, #{operation}, #{operator}, NOW())")
    int insert(AuthOperationLog log);
}