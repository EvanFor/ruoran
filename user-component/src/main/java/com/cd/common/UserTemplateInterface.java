package com.cd.common;

import com.cd.common.domain.User;
import com.cd.common.util.Page;

public interface UserTemplateInterface
{
	public Page<User> queryForPage(int pageNum, int pageSize);
	
	public User add(User user);
	
	public User delete(Integer id);
	
	public User update(User user);
	
	public User find(Integer id);
	
	public User login(User user);
	
	public Long count();
}
