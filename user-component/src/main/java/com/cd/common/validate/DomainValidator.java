package com.cd.common.validate;

public interface DomainValidator
{
	public ValidateResult validateExtra(Object domain);
	
	public ValidateResult validate(Object domain, Class<?>... groups);
	
	public ValidateResult validateProperty(Object domain, String property, Class<?>... groups);
}
