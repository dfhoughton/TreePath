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

/**
 * {@link Selector} for the . expression.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class SelfSelector<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	public SelfSelector(Match predicates, Forester<N> f) {
		super(predicates, f);
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		List<N> list = new ArrayList<N>(1);
		list.add(n);
		return list;
	}
}
