package com.bjz.portal.spring.springDataRedis.vo;

import com.bjz.common.pojo.User;

/**
 * @Classname UserHolder
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public class UserHolder {

    private static final ThreadLocal<User> t1 = new ThreadLocal<>();



    public static User getUser() {
        return t1.get();
    }

    public static void saveUser(User user){
        t1.set(user);
    }

    public static void removeUser(){
        t1.remove();
    }


}
