package com.bjz.portal.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjz.portal.common.pojo.SystemMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalMapper extends BaseMapper<SystemMessage> {

//    @Select("select * from message where id = #{id}")
    SystemMessage findById(Long id);

    IPage<SystemMessage> pageMessage(Page<SystemMessage> queryPage, @Param("keyword") String keyword);

}
