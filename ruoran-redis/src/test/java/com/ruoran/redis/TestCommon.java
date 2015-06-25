package com.ruoran.redis;

 

import static com.ruoran.Util.print;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class TestCommon
{
	
	static Jedis jedis = null;
	
	@BeforeClass
	public static void before()
	{
		jedis = new Jedis("192.168.1.248", 6379);
	}
	
	@Test
	public void testString()
	{
		String key0 = "name";
		String key1 = "age";
		String key2 = "sex";
		jedis.set(key0, "Evan");
		print(jedis.get(key0));
		
		jedis.set(key1, "12");
		print(jedis.get(key1));
		
		jedis.append(key0, ", Hello !");
		print(jedis.get(key0));
		
		jedis.setex(key2, 1, "男");
		print(jedis.get(key2));
		
		try
		{
			 TimeUnit.SECONDS.sleep(3);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		print(jedis.getbit(key1.getBytes(), 2));
		print(jedis.mget(key0, key1, key2));
		
		print(jedis.bitcount(key1));
		print(jedis.bitcount(key0, 2, 100));
		print(jedis.getrange(key0, 1, 3));
		
		jedis.mset(key0, "oo", key1, "xx", key2, "ooxx");
		print(jedis.mget(key0, key1, key2));
		
		jedis.setnx("nx", "nx");
		print(jedis.get("nx"));
		print(jedis.bitop(BitOP.AND, key0, key1));
		
		print(jedis.getSet("nx", "xn"));
		print(jedis.get("nx"));
		
		print(jedis.msetnx("PO", "po", "OP", "op"));
		print(jedis.mget("PO", "OP"));
		
		jedis.setrange(key2, 2, "hehe");
		print(jedis.get(key2));
		
		String key_max = "max";
		
		jedis.set(key_max, "1");
		jedis.incr(key_max);
		print(jedis.get(key_max));
		jedis.incrBy(key_max, 3);
		print(jedis.get(key_max));
		
		jedis.decr(key_max);
		print(jedis.get(key_max));
		jedis.decrBy(key_max, 2);
		print(jedis.get(key_max));
		
		jedis.incrByFloat(key_max, 2.5D);
		print(jedis.get(key_max));
		
		print(jedis.strlen(key_max));
		
		jedis.setbit(key_max, 0, "");
		print(jedis.strlen(key2));
		
	}
	
	@Test
	public void testKeys()
	{
		String key0 = "name";
		String key1 = "age";
		String key2 = "sex";
		
		print(jedis.exists(key0));
		print(jedis.pttl(key0));
		print(jedis.del(key0));
		print(jedis.keys("*"));
		print(jedis.type(key1));
		print(jedis.randomKey());
		print(jedis.randomBinaryKey());
		
		print(jedis.expire(key1, 1));
		print(jedis.expireAt(key2, 1));
		
		print(jedis.get(key1));
		print(jedis.get(key2));
		print(jedis.scan("1").getResult());
	}
	
	@Test
	public void testHash()
	{
		String key = "user";
		
		String name = "name";
		String age = "age";
		String sex = "sex";
		
		jedis.hset(key, name, "张三");
		jedis.hset(key, age, "12");
		jedis.hset(key, sex, "女");
		
		print(jedis.hget(key, name));
		print(jedis.hgetAll(key));
		print(jedis.hlen(key));
		print(jedis.hexists(key, name));
		
		print(jedis.hincrBy(key, age, 12L));
		print(jedis.hincrByFloat(key, age, 12D));
		print(jedis.hkeys(key));
		print(jedis.hvals(key));
		
		Map<String, String> user2 = new HashMap<String, String>();
		user2.put(name, "李四");
		user2.put(age, "25");
		user2.put(sex, "女");
		jedis.hmset("user2", user2);
		print(jedis.hgetAll("user2"));
		
		jedis.del(key);
		print(jedis.hgetAll(key));
		
		ScanParams sp = new ScanParams();
		sp.match("s*");
		ScanResult<Entry<String, String>> result = jedis.hscan(key, "0", sp);
		print(result.getResult());
	}
	
	@Test
	public void testList()
	{
		String key = "names";
		jedis.lpush(key, "evan", "ruoran", "hehe");
		jedis.rpush(key, "ruoran", "gaga", "yoyoex", "mimi");
		print(jedis.lrange(key, 0, jedis.llen(key)));
		
		print(jedis.lpop(key));
		print(jedis.rpop(key));
		print(jedis.lrange(key, 0, jedis.llen(key)));
		
		print(jedis.lindex(key, 1));
		print(jedis.lrange(key, 0, -1));
		print(jedis.lrange(key, 0, -2));
		print(jedis.lrange(key, 0, -3));
		
		jedis.linsert(key, LIST_POSITION.AFTER, jedis.lindex(key, 2), "OOXX");
		print(jedis.lrange(key, 0, -1));
		
		jedis.lset(key, 2, "OMG");
		print(jedis.lrange(key, 0, -1));
		
		jedis.ltrim(key, 0, 4);
		print(jedis.lrange(key, 0, -1));
		
		// jedis.lrem(key, 1, "ruoran");
		jedis.lrem(key, 2, "ruoran");
		print(jedis.lrange(key, 0, -1));
		
	}
	
	public void testSet()
	{
		
	}
	
	public void testZSet()
	{
		
	}
	
	public void testPubSub()
	{
		
	}
	
	@AfterClass
	public static void after()
	{
		jedis.close();
	}
}
