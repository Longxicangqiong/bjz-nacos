package com.bjz.portal.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bjz.common.util.Res;
import com.bjz.portal.common.model.SystemMessageModel;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.common.service.IPortalService;
import com.bjz.portal.common.service.impl.PortalServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.*;


@EnableAspectJAutoProxy(exposeProxy = true)   // 暴露代理对象
@RestController
@RequestMapping("/portal")
@Api(tags = "公共模块")
public class PortalController {

   @Autowired
   private IPortalService portalService;

   @ApiOperation("详情")
   @GetMapping("{id}")
   public Res<SystemMessage> queryById(@PathVariable("id") Long id,
                                       @RequestHeader(value = "headerWord", required = false) String headerWord) {
         // GatewayFilter：添加头信息
        System.out.println(" headerWord = " + headerWord);
       // 根据id查询消息并返回
       return portalService.queryById(id);
   }

   @ApiOperation("逻辑删除")
   @GetMapping("/logicDelete/{id}")
    public Res logicDelete(@PathVariable("id") Long id){
       return portalService.logicDelete(id);
   }

   @ApiOperation("列表查询")
   @GetMapping("/listMessage")
    public Res listMessage(SystemMessageModel systemMessageModel){
       return portalService.listMessage(systemMessageModel);
   }

    @ApiOperation("分页查询")
    @GetMapping("/pageMessage")
    public Res<IPage<SystemMessage>> pageMessage(SystemMessageModel systemMessageModel){
       return portalService.pageMessage(systemMessageModel);
   }

   @ApiOperation("修改系统消息")
   @PostMapping("/updateMessage")
   public Res updateMessage(@RequestBody SystemMessage systemMessage){
       return portalService.updateMessage(systemMessage);
   }

   @ApiOperation("测试MyBatis-Plus的乐观锁")
   @GetMapping("/testOptimisticLocker/{id}")
    public Res<SystemMessage> testOptimisticLocker(@PathVariable("id") Integer id){
       return portalService.testOptimisticLocker(id);
   }


}
