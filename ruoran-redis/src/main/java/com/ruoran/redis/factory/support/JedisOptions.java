package com.ruoran.redis.factory.support;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.ruoran.redis.ItrCallback;
import com.ruoran.redis.JedisPipelineCallback;
import com.ruoran.redis.JedisReadCallback;
import com.ruoran.redis.JedisTransactionCallback;
import com.ruoran.redis.JedisWriteCallback;

public interface JedisOptions
{
	Jedis getReadableResource(String key);
	
	Jedis getWriteableResource(String key);
	
	Object executeWrite(JedisWriteCallback callback, String key);
	
	Object executeWriteWithTransaction(JedisTransactionCallback callback, String key);
	
	Object executeWithPipeline(JedisPipelineCallback callback, String key);
	
	Object executeRead(JedisReadCallback callback, String key);
	
	List<JedisPool> getMasters();
	
	void doInAllMaster(ItrCallback callback);
	
	void close();
	
}
