package com.ruoran.redis.factory.support;

import static com.ruoran.Util.date;
import static com.ruoran.Util.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ruoran.entity.User;
import com.ruoran.kryo.KryoExt;
import com.ruoran.redis.MessageListener;
import com.ruoran.redis.factory.ShardedJedisSentinelPool;

public class TestRedisTemplate
{
	private static RedisTemplate template;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		template = new RedisTemplate();
		template.setKryo(new KryoExt(new String[] { "com.cd.entity" }));
		Set<String> sentinels = new HashSet<String>();
		sentinels.add("192.168.1.248:26379");
		sentinels.add("192.168.1.248:26380");
		sentinels.add("192.168.1.248:26381");
		template.setJedisOptions(new ShardedJedisSentinelPool(sentinels));
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		template.close();
	}
	
	@Test
	public void testSet()
	{
		// Object 
		template.setString("user", new User(1, "张三", 100F, 1000D, date(), false, 'N', 20L));
		
		// List & Set & Map
		List<User> usersList = new ArrayList<User>();
		Set<User> usersSet = new HashSet<User>();
		Map<String, User> usersMap = new HashMap<String, User>();
		for (int i = 0; i < 500; i++)
		{
			usersList.add(new User(i, "用户_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
			usersSet.add(new User(i, "用户_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
			usersMap.put("user_" + i, new User(i, "user_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		
		template.setString("usersList", usersList);
		template.setString("usersSet", usersSet);
		template.setString("usersMap", usersMap);
		
	}
	
	@Test
	public void testSetEx() throws InterruptedException
	{
		template.setString("userEx", new User(1, "张三", 100F, 1000D, date(), false, 'N', 20L), 3);
		TimeUnit.SECONDS.sleep(5);
		print(template.getString("userEx", User.class));
	}
	
	@Test
	public void testGet()
	{
		print(template.getString("user", User.class));
	}
	
	@Test
	public void testGetList0()
	{
		List<User> ul = template.getStringList("usersList", User.class);
		Collections.sort(ul);
		for (User u : ul)
		{
			print(u);
		}
	}
	
	@Test
	public void testGetList1()
	{
		List<User> us = template.getStringList("usersSet", User.class);
		Collections.sort(us);
		for (User u : us)
		{
			print(u);
		}
	}
	
	@Test
	public void testGetMap()
	{
		print(template.getStringMap("usersMap", User.class));
	}
	
	@Test
	public void testDel()
	{
		template.delKey("user");
		print(template.getString("user", User.class));
	}
	
	@Test
	public void testDelByPattern()
	{
		print(template.delKeyByPattern("user*"));
	}
	
	@Test
	public void testIncr()
	{
		print(template.counter("count"));
		print(template.getCounter("count"));
		print(template.counter("count", 5L));
		print(template.getCounter("count"));
		print(template.counter("count", -5L));
		print(template.getCounter("count"));
		print(template.counter("count", -1L));
		print(template.getCounter("count"));
	}
	
	@Test
	public void testSetList()
	{
		List<User> list = new ArrayList<User>();
		for (int i = 0; i < 15; i++)
		{
			list.add(new User(i, "用户_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		print(template.setList("rul", list));
	}
	
	@Test
	public void testPageParam()
	{
		print(template.calculatePageParams(4, 2, 5));
		print(template.calculatePageParams(5, 2, 1));
		print(template.calculatePageParams(6, 5, 2));
		print(template.calculatePageParams(8, 3, 4));
		print(template.calculatePageParams(18, 7, 2));
	}
	
	@Test
	public void testGetList()
	{
		print(template.getList("rul", 0, -1, User.class));
		print(template.getList("rul", User.class));
	}
	
	@Test
	public void testGetListPage()
	{
		print(template.getListByPage("rul", 5, 4, User.class));
	}
	
	@Test
	public void testAppendListAtTail()
	{
		List<User> list = new ArrayList<User>();
		for (int i = 100; i < 105; i++)
		{
			list.add(new User(i, "用户_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		
		print(template.appendListAtTail("rul", new User(1000, "千岁")));
		print(template.appendListAtTail("rul", list));
		print(template.listSize("rul"));
	}
	
	@Test
	public void testAppendListAtHead()
	{
		List<User> list = new ArrayList<User>();
		for (int i = 120; i < 125; i++)
		{
			list.add(new User(i, "用户_" + i, i * 100F, i * 1000D, date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		
		print(template.appendListAtHead("rul", new User(1600, "紫萱")));
		print(template.appendListAtHead("rul", list));
		print(template.listSize("rul"));
		print(template.getList("rul", User.class));
	}
	
	@Test
	public void testByte()
	{
		byte[] x = new byte[10000];
		byte[] y = new byte[10000];
		
		for (int i = 0; i < 10000; i++)
		{
			x[i] = (byte) i;
			y[i] = (byte) i;
		}
		
		long m = System.currentTimeMillis();
		System.out.println(Arrays.equals(x, y));
		long n = System.currentTimeMillis();
		System.out.println(new String(x).equals(new String(y)));
		long z = System.currentTimeMillis();
		
		System.out.println(n - m);
		System.out.println(z - n);
	}
	
	@Test
	public void testUpdateListElem()
	{
		template.updateListElem("rul", new User(1600, "紫萱"), new User(1610, "紫萱2号", 1000F));
		print(template.getList("rul", User.class));
	}
	
	@Test
	public void testRemoveListElem()
	{
		template.removeListElem("rul", new User(1000, "千岁"));
		print(template.getList("rul", User.class));
	}
	
	@Test
	public void testExists()
	{
		print(template.exists("rul"));
		print(template.exists("mama"));
	}
	
	@Test
	public void testExpire()
	{
		print(template.expire("rul", 5));
	}
	
	@Test
	public void testKeyType()
	{
		print(template.keyType("rul"));
	}
	
	@Test
	public void testSetStringList()
	{
		template.delKey("strs");
		List<String> strs = new ArrayList<String>();
		for (int i = 0; i < 20; i++)
		{
			strs.add("" + i);
		}
		template.setList("strs", strs);
		print(template.getList("strs", String.class));
	}
	
	@Test
	public void testSetStringList1()
	{
		Random r = new Random();
		char[] cs = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n' };
		template.delKey("strsx");
		List<String> strsx = new ArrayList<String>();
		for (int i = 0; i < 20; i++)
		{
			String m = "";
			for (int j = 0; j < 4; j++)
			{
				m += cs[r.nextInt(cs.length)];
			}
			strsx.add(m);
		}
		template.setList("strsx", strsx);
		print(template.getList("strsx", String.class));
	}
	
	@Test
	public void testSetIntegerList()
	{
		template.delKey("intnums");
		List<Integer> intnums = new ArrayList<Integer>();
		for (int i = 0; i < 20; i++)
		{
			intnums.add(i);
		}
		template.setList("intnums", intnums);
		print(template.getList("intnums", Integer.class));
	}
	
	@Test
	public void testSetLongList()
	{
		template.delKey("longnums");
		List<Long> longnums = new ArrayList<Long>();
		for (int i = 0; i < 20; i++)
		{
			longnums.add(new Long(i));
		}
		template.setList("longnums", longnums);
		print(template.getList("longnums", Long.class));
	}
	
	@Test
	public void testSort0()
	{
		print(template.sort("intnums", Integer.class, false));
		print(template.sort("intnums", Integer.class, true));
	}
	
	@Test
	public void testSort1()
	{
		print(template.sort("longnums", Long.class, false));
		print(template.sort("longnums", Long.class, true));
	}
	
	@Test
	public void testSort2()
	{
		print(template.sort("strs", String.class, false));
		System.out.println("---------------------------");
		print(template.sort("strs", String.class, true));
	}
	
	@Test
	public void testSort3()
	{
		print(template.sort("strsx", String.class, false));
		System.out.println("---------------------------");
		print(template.sort("strsx", String.class, true));
	}
	
	@Test
	public void testSort4()
	{
		print(template.sort("rul", User.class, false));
	}
	
	@Test
	public void testSubscribe()
	{
		template.subscribe(new MessageListener(), "user", "role");
	}
	
	@Test
	public void testPSubscribe()
	{
		template.psubscribe(new MessageListener(), "user*", "role*");
	}
	
	@Test
	public void testPublish()
	{
		template.publish("user", "add a User");
		template.publish("role", "add a Role");
	}
}
