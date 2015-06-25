package com.ruoran.redis;

import redis.clients.jedis.Jedis;

public interface ItrCallback
{
	/**
	 * 对所有的master节点进行操作
	 * @param jedis
	 */
	void doInCallback(Jedis jedis);
}
