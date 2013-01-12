/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Instantiates the predicate in {@code a[1]} and the like. This predicate
 * returns the
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 */
class IndexPredicate<N> extends Predicate<N> {
	private static final long serialVersionUID = 1L;
	private int index;

	IndexPredicate(int index) {
		this.index = index;
	}

	@Override
	Collection<N> filter(Collection<N> c, Index<N> i) {
		int in = index;
		if (in < 0)
			in = c.size() + in;
		if (in < 0 || in >= c.size())
			return Collections.emptyList();
		List<N> filtrate = new ArrayList<N>(1);
		int j = 0;
		for (Iterator<N> k = c.iterator(); k.hasNext();) {
			N n = k.next();
			if (j++ == in) {
				filtrate.add(n);
				break;
			}
		}
		return filtrate;
	}

}
