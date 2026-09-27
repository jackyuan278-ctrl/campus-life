package com.campuslife.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

    /** 报名脚本：原子扣名额（返回 0成功 1已满 2重复） */
    @Bean
    public DefaultRedisScript<Long> signupScript(@Value("classpath:lua/signup.lua") Resource script) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(script);
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /** 取消脚本：名额回补+候补队头补位（返回 -1未报名 0无补位 >0补位用户ID） */
    @Bean
    public DefaultRedisScript<Long> cancelSignupScript(@Value("classpath:lua/cancel_signup.lua") Resource script) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(script);
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
