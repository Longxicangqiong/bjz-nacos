package com.bjz.user.commom.mapper;

import com.bjz.common.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    
    @Select("select * from system_user where id = #{id}")
    User findById(@Param("id") Long id);
}