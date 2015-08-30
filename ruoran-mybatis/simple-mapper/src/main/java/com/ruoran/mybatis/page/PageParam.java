package com.ruoran.mybatis.page;

public class PageParam
{
	public int pageNum = 1;
	public int pageSize = 10;
	
	public PageParam()
	{
	}
	
	public PageParam(int pageNum, int pageSize)
	{
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
}
