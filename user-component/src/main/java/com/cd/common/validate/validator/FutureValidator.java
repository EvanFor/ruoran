package com.cd.common.validate.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

import com.cd.common.validate.ext.Future;
 
public class FutureValidator implements ConstraintValidator<Future, Date>
{
	private SimpleDateFormat sdf;
	private Date date;
	private String pattern;
	
	@Override
	public void initialize(Future future)
	{
		this.pattern = future.pattern();
		if (this.pattern == null || this.pattern.trim().equals(""))
		{
			this.pattern = "yyyy-MM-dd";
		}
		this.sdf = new SimpleDateFormat(this.pattern);
		String value = future.value();
		if (value == null || value.trim().equals(""))
		{
			this.date = new Date();
		}
		else
		{
			try
			{
				this.date = this.sdf.parse(future.value());
			}
			catch (ParseException e)
			{
				throw new ValidationException(e.getMessage());
			}
		}
	}

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context)
	{
		if (value == null)
		{
			return true;
		}
		return value.before(this.date);
	}
	
}
