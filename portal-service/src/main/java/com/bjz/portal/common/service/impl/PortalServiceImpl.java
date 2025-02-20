package com.bjz.portal.common.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjz.common.feign.UserFeign;
import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.portal.common.mapper.PortalMapper;
import com.bjz.portal.common.model.SystemMessageModel;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.common.service.IPortalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bjz.common.constants.RedisConstants.*;

@Service
@Primary
@Transactional
public class PortalServiceImpl extends ServiceImpl<PortalMapper, SystemMessage> implements IPortalService {

    @Autowired
    private PortalMapper portalMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Override
    public Res<SystemMessage> queryById(Long id) {
        SystemMessage message = new SystemMessage();
        // 先从redis缓存中查
        String messageKey = CACHE_MESSAGE_KEY + id;
        String messageJson = stringRedisTemplate.opsForValue().get(messageKey);

        // 判断缓存是否是防止缓存穿透的空值
        if("".equals(messageJson)){
            return Res.ok(message);
        }

        if(StringUtils.isNotBlank(messageJson)){
            message = JSONUtil.toBean(messageJson, SystemMessage.class);
        }
        // 没有再从数据库查
        else {
//        Message message = portalMapper.findById(id);
             message = getById(id);
             if(null == message){
                 // 防止缓存穿透，redis和数据库都没有查询到数据
                 stringRedisTemplate.opsForValue().set(messageKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                 return Res.ok(message);
             }
             // 详情时查询数据，并写入Redis缓存中
            stringRedisTemplate.opsForValue().set(messageKey, JSONUtil.toJsonStr(message), CACHE_MESSAGE_TTL, TimeUnit.MINUTES);
        }

        // 查询用户
        Res res = userFeign.getById(message.getUserId());
        List<User> list = Res.transferData(res, new TypeReference<List<User>>() {});
        if(!list.isEmpty()){
            message.setUser(list.get(0));
        }
        // 返回
        return Res.ok(message);
    }

/*    public Message queryById(Long id) {
        // 查询订单
        Message message = portalMapper.findById(id);
        // 查询用户
//        String url = "http://localhost:8082/user/" + message.getUserId();
        String url = "http://userservice/user/" + message.getUserId();
        User user = restTemplate.getForObject(url, User.class);
        message.setUser(user);
        // 返回
        return message;
    }*/

    @Override
    public Res updateMessage(SystemMessage systemMessage) {
        if(null == systemMessage.getId()){
            return Res.error("消息主键Id不存在");
        }

        updateById(systemMessage);
        // 修改数据并删除缓存
        stringRedisTemplate.delete(CACHE_MESSAGE_KEY + systemMessage.getId());

        return Res.ok();
    }

    @Override
    public Res logicDelete(Long id) {
        removeById(id);
        return Res.ok();
    }

    @Override
    public Res listMessage(SystemMessageModel systemMessageModel) {
        // 查询部分字段
        QueryWrapper<SystemMessage> queryWrapper = new QueryWrapper<SystemMessage>()
                .select("id","name","user_id");
//        return Res.ok(list(queryWrapper));

        // 子查询(也可以实现多表查询)
        queryWrapper.inSql("id", "select id from system_message where id < 105");
//        return Res.ok(listMaps(queryWrapper));

        // LambdaQueryWrapper：方法引用防止表字段名写错
        LambdaQueryWrapper<SystemMessage> lambdaQueryWrapper = new LambdaQueryWrapper<SystemMessage>()
                .like(StringUtils.isNotBlank(systemMessageModel.getKeyword()), SystemMessage::getTitle, systemMessageModel.getKeyword());
        return Res.ok(list(lambdaQueryWrapper));
    }

    @Override
    public Res<IPage<SystemMessage>> pageMessage(SystemMessageModel systemMessageModel) {
        Page<SystemMessage> queryPage = Page.of(systemMessageModel.getPageNum(), systemMessageModel.getPageSize());
        // 排序
        queryPage.addOrder(new OrderItem("id", true));
        queryPage.addOrder(new OrderItem("price", false));

//        Page<SystemMessage> resultPage = page(queryPage, null);

        IPage<SystemMessage> resultPage = portalMapper.pageMessage(queryPage, systemMessageModel.getKeyword());

        List<SystemMessage> list = queryPage.getRecords();
        long total = queryPage.getTotal();
        long pages = queryPage.getPages();
        boolean next = queryPage.hasNext();
        boolean previous = queryPage.hasPrevious();
        return Res.ok(queryPage);
    }

    @Override
    public Res<SystemMessage> testOptimisticLocker(Integer id) {
        // 张飞将数量加10
        SystemMessage data1 = getById(id);
        data1.setNum(data1.getNum() + 10);
        // 赵云将数量减5
        SystemMessage data2 = getById(id);
        data2.setNum(data2.getNum() - 5);

        updateById(data1);
        // 注意sql语句
        boolean boo = updateById(data2);
        // 修改失败，则重试一次
        // 乐观锁（修改时检查，失败则重试：再查询最新数据并修改）、悲观锁（阻塞等待，一个一个来）
 /*       if(!boo){
            SystemMessage data3 = getById(id);
            data3.setNum(data3.getNum() - 5);
            updateById(data3);
        }*/

        // 最终的数量是多少
        return Res.ok(getById(id));
    }




}
