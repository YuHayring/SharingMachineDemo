package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@RestController
@RequestMapping("/test")
public class TestController {


    public RedisTemplate redisTemplate;

    private JwtUtil jwtUtil;

    @RequestMapping("/tokenTest")
    public String tokenTest(@RequestHeader("Authorization") String token) {
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null || JwtUtil.isTokenExpired(claims.getExpiration())) {
            return "false";
        }
        return "true";
    }

    @RequestMapping("/redisMapTest")
    public void redisMapTest() {
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
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    static class KeyLock implements Runnable {

        public static RedisTemplate redisTemplate;

        public static Map<String, Boolean> keyLock;

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

            if (lockMachine(1)) {
                System.out.println("Winner Thread:" + Thread.currentThread());
            }


        }

        private boolean lockMachine(Integer machineId) {

            String key = "ml" + machineId;

            long expires = System.currentTimeMillis() + 60 * 1000 + 1;
            String expiresStr = String.valueOf(expires); //锁到期时间
            if (redisTemplate.opsForValue().setIfAbsent(key, expiresStr)) {
                return true;
            }


            //redis里key的时间
            String currentValue = (String) redisTemplate.opsForValue().get(key);


            //判断锁是否已经过期，过期则重新设置并获取
            if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
                //设置锁并返回旧值
                String oldValue = (String) redisTemplate.opsForValue().getAndSet(key, expiresStr);
                //比较锁的时间，如果不一致则可能是其他锁已经修改了值并获取
                if (oldValue != null && oldValue.equals(currentValue)) {
                    return true;
                }
            }
            return false;
        }


    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        KeyLock.redisTemplate = redisTemplate;
        KeyLock.keyLock = new DefaultRedisMap<>("TEST", redisTemplate.opsForHash().getOperations());
    }


}
