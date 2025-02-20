package com.bjz.common.feign;

import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("userservice")
public interface UserFeign {

    @GetMapping("/user/{id}")
    Res getById(@PathVariable("id") Long id);

}
