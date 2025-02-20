package redis;

import com.bjz.portal.PortalApplication;
import com.bjz.portal.spring.springDataRedis.config.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname RedisTest
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@SpringBootTest(classes = PortalApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class RedisTest {

    @Autowired
    private RedisIdWorker redisIdWorker;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Value("${spring.redis.port}")
    public static String redisPort;



    @Test
    public void testRedisGetId() throws InterruptedException {
        CountDownLatch countDownLatch  = new CountDownLatch(20);

        try {
            // 任务：每个任务请求100次
            Runnable task = () -> {
                for (int i = 0; i < 100; i++) {
                    System.out.println(redisIdWorker.getId("order"));;
                }
                // 每执行一次任务，减一次
                countDownLatch.countDown();
            };

            long begin = System.currentTimeMillis();
            // 执行20次任务：共执行20*100=2000次
            for (int i = 0; i < 20; i++) {
                // 10个线程去异步执行
                executorService.execute(task);
//                ORDER_EXECUTOR.execute(new orderTasks());
            }
            long end = System.currentTimeMillis();
            System.out.println("time = "  +  (end - begin));

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            countDownLatch.await();
        }

    }



    class taskArgs implements Runnable{
        private String data;

        public taskArgs (String arg){
            this.data = arg;
        }

        @Override
        public void run() {
            while (true){
                try {
                    if(null != data){
                        log.info("从阻塞队列中获取订单，并创建..........");
                    }
                } catch (Exception e) {
                    log.error("处理订单异常" , e);
                }
            }
        }

    }


    /**
     * @Description
     * @Author BJZ
     * @Date
     **/
    class cache1{
        private RedisTemplate redisTemplate;

        public RedisTemplate getRedisTemplate() {
            if(null == redisTemplate){
                redisTemplate = new RedisTemplate();
            }
            return redisTemplate;
        }

        // redis配置文件
        public RedisProperties properties;
        public RedisProperties getProperties() {
            return properties;
        }
        public void setProperties(RedisProperties properties) {
            this.properties = properties;
        }



        public boolean isExist(String key){
            return getRedisTemplate().hasKey(getKey(key));
        }

        private Object getKey(String key) {
            RedisProperties.Cluster cluster = properties.getCluster();
            // key的拼接在这里专门处理
            return key;
        }

        public void delKey(String key){
            // 删除某模块下的所有key
            List<String> list = Arrays.asList(key);
            try {
                redisTemplate.delete(list);
            } catch (Exception e) {
                e.printStackTrace();
                // 再次尝试
                try {
                    list.forEach(x -> redisTemplate.delete(x));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error("删除节点失败{}", key);
                }
            }
        }
    }


}
