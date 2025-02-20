package com.bjz.portal.spring.springDataRedis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.portal.common.mapper.SystemOrderMapper;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.common.pojo.SystemOrder;
import com.bjz.portal.common.service.IPortalService;
import com.bjz.portal.spring.springDataRedis.config.RedisIdWorker;
import com.bjz.portal.spring.springDataRedis.service.IRedisOrderService;
import com.bjz.portal.spring.springDataRedis.vo.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bjz.common.constants.RedisConstants.REDIS_LOCK_KEY;

/**
 * @Classname RedisOrderServiceImpl
 * @Description 主线程查询判断数据库/Redis，线程池和JVM阻塞队列去异步插入/修改数据库，再不行就是MQ
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Slf4j
@Service
@Primary
public class RedisOrderServiceImpl implements IRedisOrderService {

    @Autowired
    private IPortalService portalService;
    @Autowired
    private SystemOrderMapper systemOrderMapper;
    @Autowired
    private RedisIdWorker redisIdWorker;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    /**
     *  lua脚本：Script
     *  DefaultRedisScript<Long>：Long为返回值类型
     */
    private static final DefaultRedisScript<Long> ORDER_SCRIPT;

    static {
        ORDER_SCRIPT = new DefaultRedisScript<>();
        // 设置脚本的位置
        ORDER_SCRIPT.setLocation(new ClassPathResource("/redis/AsynOrder.lua"));
        // 返回值类型
        ORDER_SCRIPT.setResultType(Long.class);
    }

    // 获取当前类的代理对象：
    private IRedisOrderService orderProxy ;
    // 创建订单的阻塞队列
    private BlockingQueue<SystemOrder> orderBlockingQueue = new ArrayBlockingQueue<>(512);
    // 创建订单的线程池
    private static final ExecutorService ORDER_EXECUTOR = Executors.newFixedThreadPool(8);
    // 线程任务：创建订单
    private class orderTasks implements Runnable{
        @Override
        public void run() {
            while (true){
                // 从阻塞队列中获取订单，并创建
                try {
                    asynRedisOrderTask(orderBlockingQueue.take());
                } catch (InterruptedException e) {
                    log.error("处理订单异常" , e);
                }
            }
        }
    }
    @PostConstruct
    private void initOrder(){

        ORDER_EXECUTOR.execute(new orderTasks());

/*		其他使用时，可以加锁
		synchronized(this){
			pool.execute(task);
		}*/
    }



    @Override
    public Res addRedisOrder(Long id) {
        // 因为是一人一单，所以key精确到用户：模块编码 + userId
        // value为UUID+当前线程id。因为不同jvm中的线程id可能相同

        // 当前登录人
        UserHolder.saveUser(new User(5L,"张飞","桃园"));

        // 查询商品
        SystemMessage message = portalService.getById(id);

        // todo 判断商品是否存在，是否在活动期

        if(message.getNum() < 1){
            return Res.error("库存不足");
        }

        // 获取当前登登录人ID
        Long userId = UserHolder.getUser().getId();

        // Synchronized只能保证单个微服务的线程安全问题
/*        synchronized(userId.toString().intern()){
//            return this.addOrder(id);
            // 拿到当前对象IRedisOrderService的代理对象，事务失效的场景之一
            IRedisOrderService proxy = (IRedisOrderService) AopContext.currentProxy();
            return proxy.addOrder(id);
        }*/


        // 创建自定义的分布式锁对象，因为是一人一单，所以key精确到用户
/*        RedisCustomLock redisCustomLock = new RedisCustomLock("order" + userId, stringRedisTemplate);
        boolean tryLock = redisCustomLock.tryLock(10L);
        // 获取锁失败，一人一单，不用重试，直接返回
        if (!tryLock){
            return Res.error("您已下单");
        }
        try {
             // 获取代理对象，事务失效的场景之一
            IRedisOrderService proxy = (IRedisOrderService) AopContext.currentProxy();
            return proxy.addOrder(id);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            redisCustomLock.unLock();
        }*/


        // 创建锁对象：可重入锁
        RLock rLock = redissonClient.getLock(REDIS_LOCK_KEY + "order" + userId);
        try {
//            boolean tryLock = rLock.tryLock(1, 10, TimeUnit.SECONDS);
            boolean tryLock = rLock.tryLock();
            // 获取锁失败，一人一单，不用重试，直接返回
            if (!tryLock){
                return Res.error("您已下单");
            }
            // 获取代理对象，事务失效的场景之一
            IRedisOrderService proxy = (IRedisOrderService) AopContext.currentProxy();
            return proxy.addOrder(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }
    }

    @Override
    @Transactional
    public Res addOrder(Long id) {
        //获取当前登登录人ID
        Long userId = UserHolder.getUser().getId();
        // 根据用户ID和商品ID去订单表中统计查询
        Long count = systemOrderMapper.selectCount(new LambdaQueryWrapper<SystemOrder>()
                .eq(SystemOrder::getUserId, userId).eq(SystemOrder::getDeviceId, id));
        // 高并发情况下，多个用户/请求/线程同时第一次都获未读到数据，会都下单
        if(count > 0){
            return Res.error("您已下单");
        }

        // 存储减1：高并发情况下，多个用户/请求/线程都获取到商品数量为1时，都扣减，很容易超卖
        boolean result = portalService.update()
                .setSql("num = num - 1")
                .eq("id", id)
                // cas乐观锁缺点（很容易操作失败，成功率低）：多个线程都查询到数量为5时，只有一个会成功
//                .eq("num", message.getNum())
                .gt("num", 0)
                .update();
        if(!result){
            return Res.error("扣减库存失败2222222222");
        }

        // 创建订单
        int insertOrder = systemOrderMapper.insert(SystemOrder.builder()
                .id(redisIdWorker.getId("order")).userId(userId).deviceId(id).build());
        if(0 == insertOrder){
            return Res.error("创建订单失败");
        }

        return Res.ok();
    }

    @Override
    public Res asynRedisOrder(Long id) {
        // 当前登录人
        UserHolder.saveUser(new User(5L,"张飞","桃园"));

        // 读取lua脚本，redis中判断库存是否充足、是否已经下单。校验通过则扣预除库存并预下单
        Long result = stringRedisTemplate.execute(
                ORDER_SCRIPT,  // 脚本
                Collections.emptyList(),
                UserHolder.getUser().getId().toString(), id.toString());
        int value = result.intValue();
        // 脚本结果：1通过、2库存不足、3已下单
        if(1 != value){
            return Res.error(2 == value ? "库存不足":"您已下单");
        }

        // 创建订单
        SystemOrder order = SystemOrder.builder().id(redisIdWorker.getId("order"))
                .userId(UserHolder.getUser().getId()).deviceId(id).build();
        // 获取当前类的代理对象
        orderProxy = (IRedisOrderService) AopContext.currentProxy();
        // 将订单放到阻塞队列中
        orderBlockingQueue.add(order);
        return Res.ok();
    }

    public void asynRedisOrderTask(SystemOrder order) {
        // 理论上这里不需要再加锁判断，因为lua脚本里已经判断过了，这里再做一次保障
        RLock rLock = redissonClient.getLock(REDIS_LOCK_KEY + "order" + order.getUserId());
        try {
//            boolean tryLock = rLock.tryLock(1, 10, TimeUnit.SECONDS);
            boolean tryLock = rLock.tryLock();
            // 获取锁失败，一人一单，不用重试，直接返回
            if (!tryLock){
                log.error("您已下单");
                return ;
            }

            // 当前线程是线程池开启的子线程，获取线程的代理对象
            orderProxy.asynOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }
    }

    @Override
    @Transactional
    public void asynOrder(SystemOrder order) {
        // 理论上这里不需要再加锁判断，因为lua脚本里已经判断过了，这里再做一次保障
        Long count = systemOrderMapper.selectCount(new LambdaQueryWrapper<SystemOrder>()
                .eq(SystemOrder::getUserId, order.getUserId()).eq(SystemOrder::getDeviceId, order.getId()));
        // 高并发情况下，多个用户/请求/线程同时第一次都获未读到数据，会都下单
        if(count > 0){
            log.error("您已下单");
            return;
        }

        // 存储减1：高并发情况下，多个用户/请求/线程都获取到商品数量为1时，都扣减，很容易超卖
        boolean result = portalService.update()
                .setSql("num = num - 1")
                .eq("id", order.getId())
                // cas乐观锁缺点（很容易操作失败，成功率低）：多个线程都查询到数量为5时，只有一个会成功
//                .eq("num", message.getNum())
                .gt("num", 0)
                .update();
        if(!result){
            log.info("数据库扣减库存失败");
            return ;
        }

        // 创建订单
        int insertOrder = systemOrderMapper.insert(order);
        if(0 == insertOrder){
            log.error("数据库创建订单失败");
            return;
        }
    }



    public void test(){
        try {
            if(orderBlockingQueue.remainingCapacity() == 0){
                log.error("请求/下载/查询人数太多，请稍微重试");
                return;
            }

            boolean offer = orderBlockingQueue.offer(null);
            log.info("继续执行");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            orderBlockingQueue.poll();
        }
    }

}
