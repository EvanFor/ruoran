package com.ruoran.entity.constant;

public enum AttrsStyle
{
	CamelHump("camelhump"), Upper("upper"), Lower("lower");
	
	public String style;
	
	private AttrsStyle(String style)
	{
		this.style = style;
	}
}
