package com.rookied.herostory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class UserManager {
    //public static Set<User> users = new HashSet<>(); //使用set必须对User重写equals和hashcode
    //所有用户 防止多线程冲突
    private static final Map<Integer, User> userMap = new ConcurrentHashMap<>();

    private UserManager() {
    }

    public static void addUser(User u) {
        if (u != null) {
            //防止多线程冲突
            userMap.putIfAbsent(u.getUserId(), u);
        }
    }

    public static void removeUser(int userId) {
        userMap.remove(userId);
    }

    public static Collection<User> listUser() {
        return userMap.values();
    }
}
