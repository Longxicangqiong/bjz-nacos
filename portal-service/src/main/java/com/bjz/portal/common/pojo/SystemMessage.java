package com.bjz.portal.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.bjz.common.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SystemMessage {

    // IdType：AUTO(数据库ID自增。请确保数据库设置ID自增，否则无效)
    //         NONE(未设置主键类型。默认值)
    //         INPUT(用户输入)
    //         ASSIGN_ID(通过雪花算法分配ID，主键类型为number或string)
    //         ASSIGN_UUID(分配UUID，主键类型为 string
    @TableId(type = IdType.NONE)

    private Long id;

    private String title;
    private String content;
    private Integer num;
    private Long price;

//    @TableField("user_id")
    private Long userId;

    // 逻辑删除(0未删除、1已删除)
    @TableLogic
    private Integer logicDelete;

    // MyBatis-Plus乐观锁的版本号
    @Version
    private Integer version;

    @TableField(exist = false)
    private User user;

}