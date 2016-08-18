package com.cd.common.validate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ValidateResult implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Gson gson = new Gson();
	
	protected List<String> messages = new ArrayList<String>();
	
	public ValidateResult()
	{
	}
	
	public boolean hasError()
	{
		return messages.size() > 0;
	}
	
	public String getErrorMessages()
	{
		return gson.toJson(messages);
	}
	
	public void addErrorMessage(String string)
	{
		messages.add(string);
	}
	
	public void addErrorMessage(String property, String message)
	{
		messages.add(property + ":" + message);
	}
	
}
