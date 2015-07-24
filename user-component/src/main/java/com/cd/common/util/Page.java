package com.cd.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer pageSize = 10;
	private Integer pageNum = 1;
	private Long pages = 0L;
	private Long records = 0L;
	private List<T> result = new ArrayList<T>();
	
	public Page()
	{
	}
	
	public Page(Integer pageNum, Long pages, Long records, List<T> result)
	{
		this.pageNum = pageNum;
		this.pages = pages;
		this.records = records;
		this.result = result;
	}
	
	public Page(Integer pageSize, Integer pageNum, Long pages, Long records, List<T> result)
	{
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		this.pages = pages;
		this.records = records;
		this.result = result;
	}
	
	public Integer getPageSize()
	{
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}
	
	public Integer getPageNum()
	{
		return pageNum;
	}
	
	public void setPageNum(Integer pageNum)
	{
		this.pageNum = pageNum;
	}
	
	public Long getPages()
	{
		if (pages == 0L) return this.records % this.pageSize == 0 ? (this.records / this.pageSize) : (this.records / this.pageSize + 1);
		return pages;
	}
	
	public void setPages(Long pages)
	{
		this.pages = pages;
	}
	
	public Long getRecords()
	{
		return records;
	}
	
	public void setRecords(Long records)
	{
		this.records = records;
	}
	
	public List<T> getResult()
	{
		return result;
	}
	
	public void setResult(List<T> result)
	{
		this.result = result;
	}
}
