package com.harsh.system.design.concepts.ratelimiter.utils;

import com.harsh.system.design.concepts.ratelimiter.model.RateLimiterUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyPersonalRateLimiter {
    public static final int RATE_LIMIT_TIME_REFRESH_IN_SECONDS = 6000;

    public static final int MAX_REQUESTS_PER_USER = 10;

    private static Map<String, RateLimiterUser> rateLimiterUserMap;

    public MyPersonalRateLimiter() {
        rateLimiterUserMap = new HashMap<>();
    }

//    public static void main(String[] args) throws InterruptedException {
//        LocalDateTime dateTime1 = LocalDateTime.now();
//        Thread.sleep(5000);
//        LocalDateTime dateTime2 = LocalDateTime.now();
//
//        System.out.println(dateTime1.getSecond() - dateTime2.getSecond());
//    }

    private String initializeUser(String username, String userKey) {
        RateLimiterUser rateLimiterUser = new RateLimiterUser();
        rateLimiterUser.setUsername(username);
        rateLimiterUser.setUserKey(userKey);
        rateLimiterUser.setUserStartTime(LocalDateTime.now());
        rateLimiterUser.setTotalUserRequestsTillNow(0);

        rateLimiterUserMap.put(userKey, rateLimiterUser);

        return "User initialized at time " + rateLimiterUser.getUserStartTime();
    }


    public boolean hasUserCrossedLimit(String userKey) {
        RateLimiterUser rateLimiterUser = rateLimiterUserMap.get(userKey);
        boolean isWithinTimeLimit = true;
        if(LocalDateTime.now().getSecond() - rateLimiterUser.getUserStartTime().getSecond() > 10) {
            rateLimiterUser.setUserStartTime(LocalDateTime.now());
            isWithinTimeLimit = false;
        }
        return (rateLimiterUser.getTotalUserRequestsTillNow() == 10) && isWithinTimeLimit;
    }
}
