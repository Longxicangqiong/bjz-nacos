package com.bjz.user.commom.controller;

import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.user.commom.service.UserService;
import com.bjz.user.config.NacosConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
//@RefreshScope  //配置热更新
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NacosConfig nacosConfig;

//    @Value("${pattern.dateformat}")
//    public String dateformat;

    /**
     * 路径： /user/1
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public Res<List<User>> queryById(@PathVariable("id") Long id,
                                     @RequestHeader(value = "headerWord",required = false) String headerWord) {
        // GatewayFilter：添加头信息
        System.out.println(" headerWord = " + headerWord);
        User user = userService.queryById(id);
        return Res.ok(Lists.newArrayList(user));
    }

    @GetMapping("/getNacosConfig")
    public NacosConfig getNacosConfig(){
        nacosConfig.setDateFormat(LocalDateTime.now().format(DateTimeFormatter.ofPattern(nacosConfig.getDateFormat())));
        return nacosConfig;
    }

}
