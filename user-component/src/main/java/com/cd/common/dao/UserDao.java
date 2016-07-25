package com.cd.common.dao;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ValidationException;

import org.springframework.stereotype.Repository;

import com.cd.common.CountChangeListener;
import com.cd.common.UserTemplateInterface;
import com.cd.common.domain.User;
import com.cd.common.util.Page;

@Repository
public class UserDao implements UserTemplateInterface
{
	
	protected static Map<Integer, User> dataBase = new LinkedHashMap<Integer, User>();
	
	static String xing = "王李张刘陈杨黄赵吴周徐孙马朱胡郭何高林罗郑梁谢宋唐许韩冯邓曹彭曾萧田董潘袁于蒋蔡余杜叶程苏魏吕丁任沈姚卢姜崔钟谭陆汪范金石廖贾夏韦傅方白邹孟熊秦邱江尹薛阎段雷侯龙史陶黎贺顾毛郝龚邵万钱严覃武戴莫孔向汤";
	static String[] ming = { "正", "阳", "斌", "龙", "旭", "林", "玫", "琪", "熊", "兵", "威", "伟", "琼", "志", "文", "武", "栋", "欣", "楠", "虎", "贝贝", "娜娜", "静静", "婷婷", "丹丹", "蕾蕾", "蓓蓓", "凯", "潇", "紫萱", "子轩", "新余", "馨予", "静蕾", "磊", "刚", "梅", "媚", "丽丽", "莉莉", "佳", "正宇", "晨", "晓雪" };
	static String letters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static Integer initRecords = 10000;
	
	static SecureRandom random = new SecureRandom();
	
	static
	{
		init(initRecords);
	}
	
	public static void init(long total)
	{
		dataBase.clear();
		for (int i = 1; i <= total; i++)
		{
			User user = new User(i, getUsername(), "11111111", getName(), random.nextBoolean(), i * 1000F + random.nextDouble(), (short) random.nextInt(2), random.nextFloat() * 1000);
			long birthday = getTime();
			user.setBirthday(new Date(birthday));
			user.setAge(Math.round(((new Date().getTime() - birthday) / 1000 / 3600 / 24 / 365)));
			dataBase.put(i, user);
		}
		
		dataBase.get(1).setUsername("admin");
		dataBase.get(2).setUsername("supervisor");
		dataBase.get(3).setUsername("iloveyou");
		dataBase.get(4).setUsername("forgetme");
		dataBase.get(5).setUsername("guest");
		dataBase.get(6).setUsername("forever");
		dataBase.get(7).setUsername("alone");
		dataBase.get(8).setUsername("number9");
	}
	
	static String getName()
	{
		return xing.charAt(random.nextInt(xing.length())) + ming[random.nextInt(ming.length)];
	}
	
	static String getUsername()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++)
		{
			sb.append(letters.charAt(random.nextInt(letters.length())));
		}
		return sb.toString();
	}
	
	static long getTime()
	{
		return (long) (random.nextDouble() * 1000000000000L + (random.nextInt(3) % 2 == 0 ? random.nextInt(100000) : random.nextInt(10000000)));
	}
	
	public Page<User> queryForAll()
	{
		
		return this.queryForPage(1, this.count().intValue());
	}
	
	public Page<User> queryForPage(int pageNum, int pageSize)
	{
		List<User> data = new ArrayList<User>();
		
		List<Integer> ids = determineThePage(pageNum, pageSize);
		for (Integer id : ids)
		{
			data.add(dataBase.get(id));
		}
		
		Page<User> page = new Page<>();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setRecords(this.count());
		page.setResult(data);
		return page;
	}
	
	private List<Integer> determineThePage(int pageNum, int pageSize)
	{
		List<Integer> ids = new ArrayList<>(dataBase.keySet());
		int start = (pageNum - 1) * pageSize;
		int end = pageNum * pageSize > ids.size() ? ids.size() : pageNum * pageSize;
		return ids.subList(start, end);
	}
	
	public User findById(Integer id)
	{
		return dataBase.get(id);
	}
	
	public User add(User user)
	{
		initRecords += 1;
		user.setId(initRecords);
		dataBase.put(initRecords, user);
		if (listener != null) listener.onCountChange(count());
		return user;
	}
	
	public synchronized User deleteById(Integer id)
	{
		if (id <= 8) { throw new IllegalArgumentException("该用户为保留用户,不能删除"); }
		
		User user = dataBase.get(id);
		if (user != null)
		{
			dataBase.remove(id);
			if (listener != null) listener.onCountChange(count());
			return user;
		}
		else throw new NullPointerException("Id不存在/该用户为保留用户,不能删除");
	}
	
	public User update(User user)
	{
		Integer id = user.getId();
		if (null == id)
		{
			throw new NullPointerException("Id不存在");
		}
		else
		{
			dataBase.put(id, user);
			return user;
		}
	}
	
	public Long count()
	{
		return Long.valueOf(dataBase.size());
	}
	
	private CountChangeListener listener;
	
	public void setCountChangeListener(CountChangeListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public User login(User user) throws ValidationException
	{
		String userName = user.getUsername();
		String passWord = user.getPassword();
		for (int i = 1; i <= 8; i++)
		{
			User x = dataBase.get(i);
			if (x.getUsername().equals(userName) && x.getPassword().equals(passWord)) { return x; }
		}
		return null;
	}
	
}
