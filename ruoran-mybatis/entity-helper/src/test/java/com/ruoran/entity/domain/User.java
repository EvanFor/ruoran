package com.ruoran.entity.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Table(name = "user")
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OrderBy("desc")
	private String name;
	private Integer age;
	private Short gender;
	
	private IdCard idCard;
	
	public User()
	{
	}
	
	public Long getId()
	{
		return id;
	}
	
	public void setId(Long id)
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
	
	public Short getGender()
	{
		return gender;
	}
	
	public void setGender(Short gender)
	{
		this.gender = gender;
	}
	
	public IdCard getIdCard()
	{
		return idCard;
	}
	
	public void setIdCard(IdCard idCard)
	{
		this.idCard = idCard;
	}
	
	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + ", idCard=" + idCard + "]";
	}
}
