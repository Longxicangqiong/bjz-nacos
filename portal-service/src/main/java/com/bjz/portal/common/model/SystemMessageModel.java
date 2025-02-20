package com.bjz.portal.common.model;

import com.bjz.portal.common.pojo.SystemMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname SystemMessageModel
 * @Description 请求参数
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemMessageModel extends SystemMessage {

    /**
     * 分页参数
     **/
    private Integer pageNum = 0;
    private Integer pageSize = 10;


    @ApiModelProperty("关键字")
    private String keyword;

}
