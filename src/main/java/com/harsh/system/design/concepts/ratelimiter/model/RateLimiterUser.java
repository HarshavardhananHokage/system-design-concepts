package com.harsh.system.design.concepts.ratelimiter.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RateLimiterUser {

    private String username;
    private String userKey;

    private LocalDateTime userStartTime;

    private int totalUserRequestsTillNow;
}
