package com.cd.common.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String name;
	
	private String username;
	
	private String password;
	
	private Integer age;
	
	private Boolean married;
	
	private Double money;
	
	private Short gender;
	
	private Float salary;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	
	@JsonInclude(Include.NON_NULL)
	private Card card;
	
	public User(String username, String password, String name, Integer age, Boolean married, Double money, Short gender, Float salary, Date birthday)
	{
		this.username = username;
		this.password = password;
		this.name = name;
		this.age = age;
		this.married = married;
		this.money = money;
		this.gender = gender;
		this.salary = salary;
		this.birthday = birthday;
	}
	
	public User(Integer id, String username, String password, String name, Boolean married, Double money, Short gender, Float salary)
	{
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.married = married;
		this.money = money;
		this.gender = gender;
		this.salary = salary;
	}
	
	public User(Integer id, String username, String password, String name, Integer age, Boolean married, Double money, Short gender, Float salary, Date birthday)
	{
		this(username, password, name, age, married, money, gender, salary, birthday);
		this.id = id;
	}
	
	public Boolean getMarried()
	{
		return married;
	}
	
	public void setMarried(Boolean married)
	{
		this.married = married;
	}
	
	public Double getMoney()
	{
		return money;
	}
	
	public void setMoney(Double money)
	{
		this.money = money;
	}
	
	public Short getGender()
	{
		return gender;
	}
	
	public void setGender(Short gender)
	{
		this.gender = gender;
	}
	
	public Float getSalary()
	{
		return salary;
	}
	
	public void setSalary(Float salary)
	{
		this.salary = salary;
	}
	
	public Date getBirthday()
	{
		return birthday;
	}
	
	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}
	
	public User()
	{
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
	
	public Integer getAge()
	{
		return age;
	}
	
	public void setAge(Integer age)
	{
		this.age = age;
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public void setCard(Card card)
	{
		this.card = card;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", username=" + username + ", password=" + password + ", age=" + age + ", married=" + married + ", money=" + money + ", gender=" + gender + ", salary=" + salary + ", birthday=" + birthday + ", card=" + card + "]";
	}
}
