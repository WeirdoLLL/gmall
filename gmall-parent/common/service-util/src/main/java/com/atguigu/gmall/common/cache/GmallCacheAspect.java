package com.atguigu.gmall.common.cache;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 增强方法 Around环绕监听
     * @param point
     * @return
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.JavaGmallCache)")//监听哪个对象? 这里写的是被自定义注解修饰的对象
    public Object cacheAroundAdvice(ProceedingJoinPoint point){//point连接点 传入这个参数 还有点不太懂
        //定义目标方法前置操作
        //返回结果初始化
        Object result = null;
        try {
            //获取方法参数 数组类型 [46]
            Object[] args = point.getArgs();
            //获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取方法上的指定注解对象
            JavaGmallCache javaGmallCache = signature.getMethod().getAnnotation(JavaGmallCache.class);
            // 获取注解对象的前缀
            String prefix = javaGmallCache.prefix();
            // 从缓存中获取数据 拼接前缀和参数 skuInfo:[46]
            String key = prefix+Arrays.asList(args).toString();
            // 获取缓存数据
            result = cacheHit(signature, key);
            //判断redis有没有数据
            if (result!=null){
                // 缓存有数据
                return result;
            }
            // 初始化分布式锁
            RLock lock = redissonClient.getLock(key + ":lock");
            //boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)){
               try {
                   try {
                       //执行目标方法:查询数据库
                       result = point.proceed(point.getArgs());
                       // 执行目标方法 后置操作
                       //防止缓存穿透
                       if (null==result){
                           // 数据库也没有
                           //result = new Object();
                           Constructor constructor = signature.getReturnType().getConstructor();
                           result = constructor.newInstance();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result),300,TimeUnit.SECONDS);
                       }else{
                           //数据库有
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result),24*60*60,TimeUnit.SECONDS);
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从redis中获取数据
     * @param signature:签名
     * @param key:getSkuInfo:[46]
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        //判断是否为空
        if (StringUtils.isNotBlank(cache)) {
            // 获取方法的返回结果类型
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        return null;
    }

}
