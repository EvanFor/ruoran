package com.cd.common.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.cd.common.validate.ext.Past;
import com.cd.common.validate.groups.Add;
import com.cd.common.validate.groups.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

// 先验证Add分组，如果有错误立即返回而不会验证Update分组，接着如果Add分组验证通过了，那么才去验证Update分组，最后指定User.class表示那些没有分组的在最后
@GroupSequence({ Add.class, Update.class, User.class })
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	@NotNull(groups = { Add.class, Update.class })
	@NotEmpty(groups = { Add.class, Update.class })
	@Size(min = 4, max = 30)
	private String name;
	
	private String username;
	
	private String password;
	
	@Range(max = 120, min = 1, groups = Add.class)
	private Integer age;
	
	@NotNull(groups = Add.class)
	private Boolean married;
	
	@NotNull(groups = { Update.class })
	private Double money;
	
	@NotNull(groups = Add.class)
	private Short gender;
	
	@NotNull(groups = { Update.class })
	@Min(200)
	private Float salary;
	
	@Past(value = "2014-12-24")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	
	@NotNull
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Card card;
	
	// 关联校验也适用于集合类型的字段,
	// 也就是说,任何下列的类型:数组,实现了java.lang.Iterable接口(例如Collection,List 和
	// Set),实现了java.util.Map接口
	
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
