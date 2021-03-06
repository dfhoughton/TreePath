/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::* or /preceding::*[&#064;attribute],
 * where this is the first step in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class RootAxisWildcard<N> extends AxisSelector<N> {
	private static final long serialVersionUID = 1L;

	RootAxisWildcard(String axisName, Match arguments, Forester<N> f) {
		super(axisName, arguments, f);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(i.root, axis, (NodeTest<N>) TrueTest.test(), i);
	}

}
