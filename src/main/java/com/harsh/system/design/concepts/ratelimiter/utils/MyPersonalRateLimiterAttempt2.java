package com.harsh.system.design.concepts.ratelimiter.utils;

import com.harsh.system.design.concepts.ratelimiter.model.RateLimiterUser;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyPersonalRateLimiterAttempt2 {
    public static final int RATE_LIMIT_TIME_REFRESH_IN_SECONDS = 6000;

    public static final int MAX_REQUESTS_PER_USER = 10;

    public static final int INVALIDATE_USER_IN_SECONDS = 12000;

    private static Map<String, RateLimiterUser> rateLimiterUserMap;

    public MyPersonalRateLimiterAttempt2() {
        rateLimiterUserMap = new ConcurrentHashMap<>();
    }

    public RateLimiterUser registerUser(String username, String userKey) {
        RateLimiterUser rateLimiterUser = new RateLimiterUser();
        rateLimiterUser.setUsername(username);
        rateLimiterUser.setUserKey(userKey);
        rateLimiterUser.setUserStartTime(LocalDateTime.now());
        rateLimiterUser.setTotalUserRequestsTillNow(0);

        rateLimiterUserMap.put(userKey, rateLimiterUser);

        return rateLimiterUser;
    }

    public Map<String, RateLimiterUser> getCurrentHashMap() {
        return rateLimiterUserMap;
    }

    public void invalidateUserMap() {
        rateLimiterUserMap.forEach((key, rateLimiterUser) -> {
            if(durationBetweenInMills(LocalDateTime.now(), rateLimiterUser.getUserStartTime()) > INVALIDATE_USER_IN_SECONDS) {
                rateLimiterUserMap.remove(key);
            }
        });


    }

    private long durationBetweenInMills(LocalDateTime endTime, LocalDateTime startTime) {
        return Math.abs(Duration.between(endTime, startTime).toMillis());
    }

    public boolean checkUserLimitReach(String userKey) {
        RateLimiterUser currRateLimiterUser = getIfNotCreateRateLimitUser(userKey);
        if(durationBetweenInMills(LocalDateTime.now(), currRateLimiterUser.getUserStartTime()) > RATE_LIMIT_TIME_REFRESH_IN_SECONDS) {
            currRateLimiterUser.setUserStartTime(LocalDateTime.now());
            currRateLimiterUser.setTotalUserRequestsTillNow(0);
        }

        if(currRateLimiterUser.getTotalUserRequestsTillNow() == 10 &&
                durationBetweenInMills(LocalDateTime.now(), currRateLimiterUser.getUserStartTime()) < RATE_LIMIT_TIME_REFRESH_IN_SECONDS) {
            return false;
        } else {
            currRateLimiterUser.setTotalUserRequestsTillNow(currRateLimiterUser.getTotalUserRequestsTillNow() + 1);
        }
        rateLimiterUserMap.put(userKey, currRateLimiterUser);
        return true;
    }

    private RateLimiterUser getIfNotCreateRateLimitUser(String userKey) {
        RateLimiterUser rateLimiterUser = null;
        if(rateLimiterUserMap.containsKey(userKey)) {
            rateLimiterUser = rateLimiterUserMap.get(userKey);
        } else {
            rateLimiterUser = registerUser(userKey + "-auto-created-username", userKey);
        }

        return rateLimiterUser;
    }
}
