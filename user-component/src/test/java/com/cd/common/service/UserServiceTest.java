package com.cd.common.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cd.common.domain.User;
import com.cd.common.util.Page;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserServiceTest extends AbstractJUnit4SpringContextTests
{
	@Autowired
	private UserService userService;
	
	@Test
	public void testQueryForPage()
	{
		Page<User> page = userService.queryForPage(1, 10);
		Assert.assertEquals(10, page.getResult().size());
	}
	
	@Test
	public void testFind()
	{
		Assert.assertNotNull(userService.findById(1));
	}
	
	@Test(expected = javax.validation.ValidationException.class)
	public void testAdd()
	{
		User user = new User();
		userService.add(user);
		Assert.assertNotNull(user.getId());
	}
	
	@Test
	public void testDelete()
	{
		Long x = userService.count();
		userService.deleteById(33);
		Long y = userService.count();
		Assert.assertEquals(1, x - y);
	}
	
	@Test
	public void testUpdate()
	{
		User user = userService.findById(30);
		user.setName("xxx");
		userService.update(user);
		User now = userService.findById(30);
		Assert.assertEquals("xxx", now.getName());
	}
}
