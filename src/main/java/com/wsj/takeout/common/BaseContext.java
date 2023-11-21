package com.wsj.takeout.common;

/**
 * 基于threadLocal封装工具类，用于用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal= new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
