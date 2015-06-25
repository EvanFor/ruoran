package com.ruoran.redis;

import redis.clients.jedis.Jedis;

public interface JedisWriteCallback
{
	/**
	 * 只写
	 * @param shardedJedis
	 * @param key
	 * @return
	 */
	Object doInCallback(Jedis jedis, byte[] key);
	
}