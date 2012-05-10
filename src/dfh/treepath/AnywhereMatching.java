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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

/**
 * {@link Selector} implementing <code>//~foo~</code> and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AnywhereMatching<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;
	private final NodeTest<N> test;
	private final boolean first;

	AnywhereMatching(String pattern, Match arguments, Forester<N> f,
			boolean first) {
		super(arguments, f);
		this.first = first;
		try {
			final Pattern p = Pattern.compile(pattern);
			test = new NodeTest<N>() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean passes(N n, Index<N> i) {
					return i.f.matchesTag(n, p);
				}
			};
		} catch (PatternSyntaxException e) {
			throw new PathException("could not compile " + pattern
					+ " as a regular expression", e);
		}
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, first ? Axis.descendantOrSelf : Axis.descendant,
				test, i);
	}

}
