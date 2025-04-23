package com.harsh.system.design.concepts.ratelimiter.tests;

import com.harsh.system.design.concepts.ratelimiter.model.RateLimiterUser;
import com.harsh.system.design.concepts.ratelimiter.utils.MyPersonalRateLimiterAttempt2;

import java.util.Map;

public class BasicRateLimiterTestingClass {

    public static final String TEST_USER_1 = "test-user-1";
    public static final String TEST_USER_1_KEY = "test-user-1-key";

    public static final String TEST_USER_2 = "test-user-2";
    public static final String TEST_USER_2_KEY = "test-user-2-key";

    public static final String TEST_USER_3 = "test-user-3";
    public static final String TEST_USER_3_KEY = "test-user-3-key";

    public static final String TEST_USER_4_KEY = "test-user-4-key";

    static MyPersonalRateLimiterAttempt2 rateLimiter;
    static RateLimiterUser rateLimiterUser;

    public static void main(String[] args) throws InterruptedException {
        rateLimiter = new MyPersonalRateLimiterAttempt2();
        registerUserTest();
//        checkUserLimitTest();
//        invalidateUserCheck();
        unregisteredUserTest();
    }

    public static void registerUserTest() {
        rateLimiterUser = rateLimiter.registerUser(TEST_USER_1, TEST_USER_1_KEY);

        if(rateLimiterUser.getUserKey().equals(TEST_USER_1_KEY) &&
                rateLimiterUser.getUsername().equals(TEST_USER_1) &&
                rateLimiterUser.getTotalUserRequestsTillNow() == 0) {
            System.out.println("User register success");
        } else {
            System.out.println("User register failure");
        }
    }

    public static void checkUserLimitTest() throws InterruptedException {
        for(int i = 0; i < 11; i++) {
            System.out.println("User allowed to call API? "+rateLimiter.checkUserLimitReach(TEST_USER_1_KEY));
        }

        Thread.sleep(6000);

        for(int i = 0; i < 20; i++) {
            System.out.println("User allowed to call API? "+rateLimiter.checkUserLimitReach(TEST_USER_1_KEY));
        }
    }

    public static void invalidateUserCheck() throws InterruptedException {
        RateLimiterUser rateLimiterUser2 = rateLimiter.registerUser(TEST_USER_2, TEST_USER_2_KEY);
        RateLimiterUser rateLimiterUser3 = rateLimiter.registerUser(TEST_USER_2, TEST_USER_2_KEY);

        Thread.sleep(6000);
        rateLimiter.checkUserLimitReach(TEST_USER_3_KEY);
        rateLimiter.checkUserLimitReach(TEST_USER_1_KEY);

        Thread.sleep(6000);

        rateLimiter.invalidateUserMap();

        Map<String, RateLimiterUser> rateLimiterUserMap = rateLimiter.getCurrentHashMap();

        System.out.println("Printing Active User List After Clean Up");

        printMap(rateLimiterUserMap);

        if(rateLimiterUserMap.containsKey(TEST_USER_2_KEY))
            System.out.println("Invalidate failed");
        else
            System.out.println("Invalidate success");
    }

    private static void printMap(Map<String, RateLimiterUser> rateLimiterUserMap) {
        rateLimiterUserMap.forEach((key, rateLimiterUser) ->
                System.out.println("User key: " +key + ". User profile: " +rateLimiterUser));
    }

    public static void unregisteredUserTest() {
        rateLimiter.checkUserLimitReach(TEST_USER_4_KEY);

        Map<String, RateLimiterUser> currentHashMap = rateLimiter.getCurrentHashMap();
        printMap(currentHashMap);
        String result = currentHashMap.containsKey(TEST_USER_4_KEY) ? "Unregistered User works" : "Unregistered user fails";

        System.out.println(result);
    }
}
