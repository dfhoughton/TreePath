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
 * Implements expressions such as //* or //*[foo]
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AnywhereWildcard<N> extends WildcardSelector<N> {
	private static final long serialVersionUID = 1L;
	private final boolean first;

	AnywhereWildcard(Match predicates, Forester<N> f, boolean first) {
		super(predicates, f);
		this.first = first;
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		return i.f.axis(n, first ? Axis.descendantOrSelf : Axis.descendant,
				test, i);
	}

}
