package com.bjz.portal.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bjz.common.util.Res;
import com.bjz.portal.common.model.SystemMessageModel;
import com.bjz.portal.common.pojo.SystemMessage;

/**
 * @Classname IPortalService
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public interface IPortalService extends IService<SystemMessage> {

    public Res<SystemMessage> queryById(Long id);

    /**
     * @MethodName logicDelete
     * @Description 逻辑删除
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res logicDelete(Long id);

    /**
     * @MethodName listMessage
     * @Description  列表查询
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     *
     * @param systemMessageModel*/
    Res listMessage(SystemMessageModel systemMessageModel);

    /**
     * @MethodName pageMessage
     * @Description 分页查询
     * @Param: systemMessageModel
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res<IPage<SystemMessage>> pageMessage(SystemMessageModel systemMessageModel);

    /**
     * @MethodName testOptimisticLocker
     * @Description 测试MyBatis-Plus的乐观锁
     *             乐观锁（修改时检查，失败则重试：再查询最新数据并修改）、悲观锁（阻塞等待，一个一个来）
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res<SystemMessage> testOptimisticLocker(Integer id);

    /**
     * @MethodName updateMessage
     * @Description 修改系统消息
     * @Param: systemMessage
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res updateMessage(SystemMessage systemMessage);
}
