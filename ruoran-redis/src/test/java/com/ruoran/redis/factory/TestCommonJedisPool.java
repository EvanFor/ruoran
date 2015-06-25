package com.ruoran.redis.factory;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.util.SafeEncoder;

import com.ruoran.redis.JedisPipelineCallback;
import com.ruoran.redis.JedisReadCallback;
import com.ruoran.redis.JedisTxCallbackAdapter;
import com.ruoran.redis.JedisWriteCallback;

public class TestCommonJedisPool
{
	private static CommonJedisPool pool;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		pool = new CommonJedisPool("192.168.1.248", 6379);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		pool.close();
	}
	
	@Test
	public void testGetNativeJedis()
	{
		Jedis jedis = pool.getNativeJedis();
		jedis.set("user.name", "张三");
		Assert.assertEquals("张三", jedis.get("user.name"));
	}
	
	@Test
	public void testExecuteWrite()
	{
		Assert.assertEquals("OK", pool.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.set(key, SafeEncoder.encode("12"));
			}
		}, "user.age"));
	}
	
	@Test
	public void testExecuteRead()
	{
		byte[] ret = (byte[]) pool.executeRead(new JedisReadCallback()
		{
			@Override
			public byte[] doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.get(key);
			}
		}, "user.age");
		
		Assert.assertEquals("12", SafeEncoder.encode(ret));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteWriteWithTransaction()
	{
		Object x = pool.executeWriteWithTransaction(new JedisTxCallbackAdapter()
		{
			@Override
			public boolean doInTransaction(Transaction tx, byte[] key, Object ret)
			{
				tx.incr(key);
				return super.doInTransaction(tx, key, ret);
			}
		}, "user.age");
		
		Assert.assertEquals(13, ((List<Long>) x).get(0).intValue());
	}
	
	@Test
	public void testExecuteWithPipeline()
	{
		Object ret = pool.executeWithPipeline(new JedisPipelineCallback()
		{
			@Override
			public boolean doInPipeline(Pipeline pipeline, byte[] key)
			{
				pipeline.append(key, SafeEncoder.encode("123"));
				pipeline.get(key);
				pipeline.incr("user.age");
				return true;
			}
		}, "user.name");
		
		System.out.println(ret);
	}
}
