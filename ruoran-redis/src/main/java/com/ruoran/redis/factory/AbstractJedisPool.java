package com.ruoran.redis.factory;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.util.SafeEncoder;

import com.ruoran.redis.ItrCallback;
import com.ruoran.redis.JedisPipelineCallback;
import com.ruoran.redis.JedisReadCallback;
import com.ruoran.redis.JedisTransactionCallback;
import com.ruoran.redis.JedisWriteCallback;
import com.ruoran.redis.factory.support.JedisOptions;

public abstract class AbstractJedisPool implements JedisOptions
{
	@Override
	public Object executeWrite(JedisWriteCallback callback, String key)
	{
		Jedis shard = this.getWriteableResource(key);
		return callback.doInCallback(shard, SafeEncoder.encode(key));
	}
	
	@Override
	public Object executeRead(JedisReadCallback callback, String key)
	{
		Jedis shard = this.getReadableResource(key);
		return callback.doInCallback(shard, SafeEncoder.encode(key));
	}
	
	@Override
	public Object executeWriteWithTransaction(JedisTransactionCallback callback, String key)
	{
		Jedis shard = this.getWriteableResource(key);
		Transaction tx = new Transaction(shard.getClient());
		shard.multi();
		Object before = callback.beforeTransaction(tx, SafeEncoder.encode(key));
		boolean doit = callback.doInTransaction(tx, SafeEncoder.encode(key), before);
		return callback.commitTransaction(tx, SafeEncoder.encode(key), doit);
	}
	
	@Override
	public Object executeWithPipeline(JedisPipelineCallback callback, String key)
	{
		Jedis shard = this.getWriteableResource(key);
		Pipeline pipeline = shard.pipelined();
		boolean ret = callback.doInPipeline(pipeline, SafeEncoder.encode(key));
		return ret ? pipeline.syncAndReturnAll() : null;
	}
	
	@Override
	public void doInAllMaster(ItrCallback callback)
	{
		List<JedisPool> masterNodes = this.getMasters();
		if (masterNodes != null)
		{
			for (JedisPool pool : masterNodes)
			{
				Jedis jedis = pool.getResource();
				// 不要在callback里面另起线程
				callback.doInCallback(jedis);
				pool.close();
			}
		}
	}
}
