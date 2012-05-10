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
import java.util.List;

import dfh.grammar.Match;

class TreePathPredicate<N> extends Predicate<N> {
	private static final long serialVersionUID = 1L;

	private final Path<N> path;

	TreePathPredicate(Match type, Forester<N> f) {
		path = f.path(type);
	}

	@Override
	Collection<N> filter(Collection<N> c, Index<N> i) {
		List<N> filtrate = new ArrayList<N>(c.size());
		for (N n : c) {
			Collection<N> c2 = path.select(n);
			if (!c2.isEmpty())
				filtrate.add(n);
		}
		return filtrate;
	}

}
