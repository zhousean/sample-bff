package com.tng.security.mobileserver.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

public class LettuceConnectionFactory {
	
	static RedisClient redisClient = null;
	static StatefulRedisConnection<String, String> redisConnection = null;


	public static StatefulRedisConnection<String, String> getConnection() {
		if (redisConnection == null) {
			redisClient = RedisClient.create("redis://localhost:6379/");
			redisConnection = redisClient.connect();
		}
		return redisConnection;
	}
	
}
