package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestRedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/29 18:44
 * 测试Redis 接口实现类
 */
@Service
public class TestRedisServiceImpl implements TestRedisService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonSingle;



    public void testRedisson(){
        //获取锁
        RLock lock = redissonSingle.getLock("lock");
        //加锁
        try {
            if (lock.tryLock(100,100,TimeUnit.SECONDS)){
                try{
                    //加锁成功操作redis++
                    Integer i = (Integer)redisTemplate.opsForValue().get("java");
                    if (null != i){
                        Thread.currentThread().getId();
                        i++;
                        redisTemplate.opsForValue().set("java", i);
                    }
                }catch (Exception e){
                    System.out.println("加锁成功 但是逻辑出现异常");
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("加锁失败 出现异常");
        }
        //加锁成功后操作redis++

        //加锁失败重试

    }

    /**
     * 测试
     */
    public void testRedis(){
        //String uuid = UUID.randomUUID().toString().replace("-", "");
        String name = Thread.currentThread().getName();
        //加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", name,10, TimeUnit.SECONDS);
        if (lock){
            //从redis中获取key=java的value
            Integer i = (Integer)redisTemplate.opsForValue().get("java");
            //若这个value不为空则加1
            if (null != i) {
                i++;
                //将这个value放回redis
                redisTemplate.opsForValue().set("java", i);
//                Object lockUUID = redisTemplate.opsForValue().get("lock");
//                if (lockUUID.equals(uuid)){
//                    //释放锁
//                    redisTemplate.delete("lock");
//                }
                DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
                script.setResultType(Long.class);
                redisTemplate.execute(script, Arrays.asList("lock"),name);
            }
            }else{
                try {
                    Thread.sleep(100);
                    testRedis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
