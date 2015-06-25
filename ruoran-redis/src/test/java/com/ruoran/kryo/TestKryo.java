package com.ruoran.kryo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ruoran.entity.User;

public class TestKryo
{
	private KryoExt kryoExt;
	
	@Before
	public void setUp()
	{
		kryoExt = new KryoExt(new String[] { "com.cd.entity" });
	}
	
	@Test
	public void test00()
	{
		User user = new User(1, "张三", 100F, 1000D, new Date(), false, 'N', 20L);
		byte[] data = kryoExt.write(user);
		User u = kryoExt.read(data, User.class);
		System.out.println(u);
	}
	
	@Test
	public void test01()
	{
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < 500; i++)
		{
			users.add(new User(i, "用户_" + i, i * 100F, i * 1000D, new Date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		byte[] data = kryoExt.write(users);
		List<User> us = kryoExt.readList(data, User.class);
		System.out.println(us);
	}
	
	@Test
	public void test02()
	{
		Set<User> users = new HashSet<User>();
		for (int i = 0; i < 500; i++)
		{
			users.add(new User(i, "user_" + i, i * 100F, i * 1000D, new Date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		byte[] data = kryoExt.write(users);
		List<User> us = kryoExt.readList(data, User.class);
		System.out.println(us);
	}
	
	@Test
	public void test03()
	{
		Map<String, User> users = new HashMap<String, User>();
		for (int i = 0; i < 500; i++)
		{
			users.put("user_" + i, new User(i, "user_" + i, i * 100F, i * 1000D, new Date(), i % 2 == 0 ? true : false, i % 2 == 0 ? 'N' : 'M', i * 1L + 20));
		}
		byte[] data = kryoExt.write(users);
		Map<String, User> us = kryoExt.readMap(data, User.class);
		System.out.println(us);
	}
}
