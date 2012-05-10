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
import java.util.List;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;

abstract class TestSelector<N> implements Selector<N> {
	private static final long serialVersionUID = 1L;
	static final MatchTest predicateMT = new MatchTest() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("predicate");
		}
	};
	protected final Predicate<N>[] predicates;

	@SuppressWarnings("unchecked")
	TestSelector(Match arguments, Forester<N> f) {
		List<Match> argList = arguments.closest(predicateMT);
		predicates = new Predicate[argList.size()];
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = Predicate.build(argList.get(i), f);
		}
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		Collection<N> candidates = candidates(n, i);
		for (Predicate<N> p : predicates) {
			candidates = p.filter(candidates, i);
			if (candidates.isEmpty())
				break;
		}
		return candidates;
	}

	protected abstract Collection<N> candidates(N n, Index<N> i);
}
