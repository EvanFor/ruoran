package com.ruoran.entity.constant;

public enum AttrsStyle
{
	Normal("normal"), CamelHump("camelhump");
	
	public String style;
	
	private AttrsStyle(String style)
	{
		this.style = style;
	}
}
