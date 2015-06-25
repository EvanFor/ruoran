package com.ruoran.redis;

import redis.clients.jedis.Pipeline;

public interface JedisPipelineCallback
{
	/**
	 * 
	 * @param pipeline
	 * @param key
	 * @return
	 */
	boolean doInPipeline(Pipeline pipeline, byte[] key);
}
