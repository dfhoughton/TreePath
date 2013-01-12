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
 * Base {@link Selector} class for steps involving the wildcard expression
 * {@code *}.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
abstract class WildcardSelector<N> implements Selector<N> {
	private static final long serialVersionUID = 1L;

	protected NodeTest<N> test;

	@SuppressWarnings("unchecked")
	WildcardSelector(Match predMatch, Forester<N> f) {
		List<Match> predList = predMatch.closest(TestSelector.predicateMT);
		final Predicate<N>[] predicates = new Predicate[predList.size()];
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = Predicate.build(predList.get(i), f);
		}
		test = new NodeTest<N>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean passes(N n, Index<N> i) {
				Collection<N> list = new ArrayList<N>(1);
				list.add(n);
				for (Predicate<N> p : predicates) {
					list = p.filter(list, i);
					if (list.isEmpty())
						return false;
				}
				return true;
			}

		};
	}
}
