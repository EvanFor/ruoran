package com.cd.common.validate;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("defaultValidator")
public class DefaultValidator implements DomainValidator
{
	@Autowired
	private Validator validator;
	
	@Override
	public ValidateResult validate(Object domain, Class<?>... groups)
	{
		return convert(validator.validate(domain, groups), new ValidateResult());
	}
	
	@Override
	public ValidateResult validateProperty(Object domain, String property, Class<?>... groups)
	{
		return convert(validator.validateProperty(domain, property, groups), new ValidateResult());
	}
	
	private ValidateResult convert(Set<ConstraintViolation<Object>> vr, ValidateResult r)
	{
		
		Iterator<ConstraintViolation<Object>> it = vr.iterator();
		while (it.hasNext())
		{
			ConstraintViolation<Object> tar = it.next();
			r.addErrorMessage(tar.getPropertyPath().toString(), tar.getMessage());
		}
		return r;
	}
	
	@Override
	public ValidateResult validateExtra(Object domain)
	{
		return validate(domain);
	}
}
