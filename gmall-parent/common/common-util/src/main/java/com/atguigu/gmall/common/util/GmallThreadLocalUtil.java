package com.atguigu.gmall.common.util;

/**
 * 购物车微服务使用的本地线程工具类
 */
public class GmallThreadLocalUtil {

    //定义本地线程对象
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 获取本地线程的数据
     * @return
     */
    public static String get(){
        return threadLocal.get();
    }

    /**
     * 存储用户名到本地线程对象
     * @param username
     */
    public static void set(String username){
        threadLocal.set(username);
    }

    /**
     * 清理本地线程中的数据
     */
    public static void clear(){threadLocal.remove();}

}
