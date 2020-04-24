package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.utils.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class RedisMapTest {


    public RedisTemplate redisTemplate;


    public static class KeyLock implements Runnable {


        public static RedisTemplate redisTemplate;

        public static Map<String, Boolean> keyLock = new DefaultRedisMap<>("TEST", redisTemplate.opsForHash().getOperations());

        private CyclicBarrier cyclicbarrier;

        public KeyLock(CyclicBarrier cyclicbarrier, int id) {
            this.cyclicbarrier = cyclicbarrier;
            this.id = id;
        }

        private int id;

        @Override
        public void run() {
            try {
                cyclicbarrier.await();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            if (null == keyLock.put("1", true)) {
                System.out.println("Winner Thread:" + Thread.currentThread());
            }


        }


    }


    @Test
    public void run() {
        int n = 50;
        CyclicBarrier barrier = new CyclicBarrier(n);
        for (int i = 0; i < n; i++) {
            new Thread(new KeyLock(barrier, i)).start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        KeyLock.redisTemplate = redisTemplate;
    }
}
