package com.cd.common.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class Card implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotNull
	@Length(min = 4, max = 18)
	private String number;
	
	@NotNull
	private Integer type;
	
	@NotNull
	@Length(min = 4, max = 18)
	private String name;
	
	private Date startTime;
	
	public Card()
	{
	}
	
	public Card(Integer type, String name, Date startTime)
	{
		super();
		this.type = type;
		this.name = name;
		this.startTime = startTime;
	}
	
	public Card(String number, Integer type, String name, Date startTime)
	{
		super();
		this.number = number;
		this.type = type;
		this.name = name;
		this.startTime = startTime;
	}
	
	public Card(Integer id, String number, Integer type, String name, Date startTime)
	{
		super();
		this.id = id;
		this.number = number;
		this.type = type;
		this.name = name;
		this.startTime = startTime;
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public Integer getType()
	{
		return type;
	}
	
	public void setType(Integer type)
	{
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}
	
	public String getNumber()
	{
		return number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	@Override
	public String toString()
	{
		return "Card [id=" + id + ", number=" + number + ", type=" + type + ", name=" + name + ", startTime=" + startTime + "]";
	}
}
