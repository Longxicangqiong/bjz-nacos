package com.bjz.portal.spring.springDataRedis.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @Classname LoginVO
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Data
@ApiModel("短信验证码登录信息")
public class LoginVO {

    @ApiModelProperty("手机号")
    public String phone;

    @ApiModelProperty("验证码")
    public String code;

    @ApiModelProperty("密码")
    public String password;


}
