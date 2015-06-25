package com.ruoran.redis.factory;

import java.util.HashSet;
import java.util.Set;

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

public class TestShardedJedisSentinelPool
{
	
	private static ShardedJedisSentinelPool pool;
	
	@BeforeClass
	public static void setUp()
	{
		Set<String> sentinels = new HashSet<String>();
		
		sentinels.add("192.168.1.248:26379");
		sentinels.add("192.168.1.248:26380");
		sentinels.add("192.168.1.248:26381");
		
		pool = new ShardedJedisSentinelPool(sentinels);
	}
	
	@AfterClass
	public static void close()
	{
		pool.close();
	}
	
	@Test
	public void test00()
	{
		pool.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				jedis.set(key, SafeEncoder.encode("123"));
				return true;
			}
		}, "my");
	}
	
	@Test
	public void test01()
	{
		byte[] o = (byte[]) pool.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.get(key);
			}
		}, "my");
		
		Assert.assertEquals("123", SafeEncoder.encode(o));
	}
	
	@Test
	public void test02()
	{
		Object o = pool.executeWriteWithTransaction(new JedisTxCallbackAdapter()
		{
			@Override
			public Object beforeTransaction(Transaction tx, byte[] key)
			{
				return tx.set(key, SafeEncoder.encode("1"));
			}
			
			@Override
			public boolean doInTransaction(Transaction tx, byte[] key, Object ret)
			{
				tx.incr(key);
				tx.incr(key);
				return true;
			}
		}, "key");
		
		System.out.println(o);
	}
	
	@Test
	public void test03()
	{
		Object o = pool.executeWriteWithTransaction(new JedisTxCallbackAdapter()
		{
			@Override
			public boolean doInTransaction(Transaction tx, byte[] key, Object ret)
			{
				tx.incr(key);
				return true;
			}
		}, "key");
		
		System.out.println(o);
	}
	
	@Test
	public void test04()
	{
		Object o = pool.executeWriteWithTransaction(new JedisTxCallbackAdapter()
		{
			@Override
			public boolean doInTransaction(Transaction tx, byte[] key, Object ret)
			{
				tx.incr(key);
				String x = tx.discard();
				return !x.equals("OK");
			}
		}, "key");
		
		System.out.println(o);
	}
	
	@Test
	public void test05()
	{
		Object o = pool.executeWithPipeline(new JedisPipelineCallback()
		{
			@Override
			public boolean doInPipeline(Pipeline pipeline, byte[] key)
			{
				pipeline.set(key, SafeEncoder.encode("100"));
				pipeline.decr(key);
				return true;
			}
		}, "key");
		
		System.out.println(o);
	}
}
