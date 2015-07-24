package com.cd.common.util.jackson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class CustomDateDeserializer extends JsonDeserializer<Date>
{
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String date = jp.getText();
		try
		{
			if (date.matches("\\d+")) { return new Date(Long.parseLong(date)); }
			return format.parse(date);
		}
		catch (ParseException e)
		{
			return null;
		}
	}
}
