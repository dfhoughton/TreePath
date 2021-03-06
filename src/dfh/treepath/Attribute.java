/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods bearing this annotation will be available to a {@link Forester} as
 * attributes in tree path expressions.
 * <p>
 * 
 * @author David F. Houghton - Apr 23, 2012
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Attribute {
	/**
	 * Name attribute goes by in tree path expressions. If left undefined, this
	 * will be the method name.
	 * 
	 * @return name attribute goes by in tree path expressions
	 */
	String value() default "";

	/**
	 * An optional brief description of the attribute.
	 * 
	 * @return an optional brief description of the attribute
	 */
	String description() default "";
}
