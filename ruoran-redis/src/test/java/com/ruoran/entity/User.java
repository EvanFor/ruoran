package com.ruoran.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Comparable<User>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Float salary;
	private Double money;
	private Date birthday;
	private Boolean married;
	private Character sex;
	private Long age;
	
	public User()
	{
	}
	
	public User(Integer id)
	{
		this.id = id;
	}
	
	public User(Integer id, String name)
	{
		this(id);
		this.name = name;
	}
	
	public User(Integer id, String name, Float salary)
	{
		this(id, name);
		this.salary = salary;
	}
	
	public User(Integer id, String name, Float salary, Double money)
	{
		this(id, name, salary);
		this.money = money;
	}
	
	public User(Integer id, String name, Float salary, Double money, Date birthday)
	{
		this(id, name, salary, money);
		this.birthday = birthday;
	}
	
	public User(Integer id, String name, Float salary, Double money, Date birthday, Boolean married)
	{
		this(id, name, salary, money, birthday);
		this.married = married;
	}
	
	public User(Integer id, String name, Float salary, Double money, Date birthday, Boolean married, Character sex)
	{
		this(id, name, salary, money, birthday, married);
		this.sex = sex;
	}
	
	public User(Integer id, String name, Float salary, Double money, Date birthday, Boolean married, Character sex, Long age)
	{
		this(id, name, salary, money, birthday, married, sex);
		this.age = age;
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Float getSalary()
	{
		return salary;
	}
	
	public void setSalary(Float salary)
	{
		this.salary = salary;
	}
	
	public Double getMoney()
	{
		return money;
	}
	
	public void setMoney(Double money)
	{
		this.money = money;
	}
	
	public Date getBirthday()
	{
		return birthday;
	}
	
	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}
	
	public Boolean getMarried()
	{
		return married;
	}
	
	public void setMarried(Boolean married)
	{
		this.married = married;
	}
	
	public Character getSex()
	{
		return sex;
	}
	
	public void setSex(Character sex)
	{
		this.sex = sex;
	}
	
	public void setAge(Long age)
	{
		this.age = age;
	}
	
	public Long getAge()
	{
		return age;
	}
	
	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", salary=" + salary + ", money=" + money + ", birthday=" + birthday + ", married=" + married + ", sex=" + sex + ", age=" + age + "]";
	}
	
	@Override
	public int compareTo(User o)
	{
		return this.id.compareTo(o.getId());
	}
}
