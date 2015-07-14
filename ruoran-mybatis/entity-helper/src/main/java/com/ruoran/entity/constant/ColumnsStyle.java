package com.ruoran.entity.constant;

public enum ColumnsStyle
{
	Normal("normal"), CamelHump("camelhump");
	
	public String style;
	
	private ColumnsStyle(String style)
	{
		this.style = style;
	}
}
