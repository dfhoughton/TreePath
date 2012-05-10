/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.io.Serializable;
import java.util.Collection;

interface Selector<N> extends Serializable {
	/**
	 * @param n
	 * @param i
	 * @return the set of nodes passing this selector's condition
	 */
	Collection<N> select(N n, Index<N> i);
}
