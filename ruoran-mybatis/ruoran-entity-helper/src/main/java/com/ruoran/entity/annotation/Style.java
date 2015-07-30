package com.ruoran.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ruoran.entity.constant.AttrsStyle;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Style
{
	AttrsStyle style() default AttrsStyle.Upper;
}
