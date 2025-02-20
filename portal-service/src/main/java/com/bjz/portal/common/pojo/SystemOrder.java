package com.bjz.portal.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Classname SystemOrder
 * @Description 订单表
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SystemOrder {

    public Long id;

    public Long userId;

    public Long deviceId;

}
