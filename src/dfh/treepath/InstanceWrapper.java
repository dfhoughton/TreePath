/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.lang.reflect.Method;

/**
 * A struct that provides {@link Method} and a means of obtaining an object to
 * call it on.
 * <p>
 * 
 * @author David F. Houghton - May 10, 2012
 */
@SuppressWarnings("rawtypes")
class InstanceWrapper {
	private final Method m;

	InstanceWrapper(Forester f, Method m) {
		this.m = m;
	}

	Forester instance(Forester f) {
		return f;
	}

	Method method() {
		return m;
	}
}
