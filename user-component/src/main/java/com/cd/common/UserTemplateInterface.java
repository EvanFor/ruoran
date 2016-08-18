package com.cd.common;

import javax.validation.ValidationException;

import com.cd.common.domain.User;
import com.cd.common.util.Page;

public interface UserTemplateInterface
{
	public Page<User> queryForPage(int pageNum, int pageSize);
	
	public User findById(Integer id);
	
	public User add(User user) throws ValidationException;
	
	public User deleteById(Integer id) throws ValidationException;
	
	public User update(User user) throws ValidationException;
	
	public User login(User user) throws ValidationException;
	
	public Long count();
}
