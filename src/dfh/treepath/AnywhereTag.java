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
import dfh.treepath.PathGrammar.Axis;

/**
 * {@link Selector} implementing {@code //foo} and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AnywhereTag<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	private final NodeTest<N> test;

	private final boolean first;

	AnywhereTag(final String tag, Match arguments, Forester<N> f, boolean first) {
		super(arguments, f);
		this.first = first;
		test = new NodeTest<N>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.hasTag(n, tag);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, first ? Axis.descendantOrSelf : Axis.descendant,
				test, i);
	}

}
