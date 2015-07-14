package com.ruoran.entity.domain;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "idcard")
public class IdCard
{
	@Id
	private Long number;
	
	private String name;
	private Date createDate;
	
	public IdCard()
	{
	}
	
	public Long getNumber()
	{
		return number;
	}
	
	public void setNumber(Long number)
	{
		this.number = number;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Date getCreateDate()
	{
		return createDate;
	}
	
	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}
	
	@Override
	public String toString()
	{
		return "IdCard [number=" + number + ", name=" + name + ", createDate=" + createDate + "]";
	}
}
