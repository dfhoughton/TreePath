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
 * For implementing predicates like {@code a[@foo = 1]}, {@code a[@foo = 'bar']}
 * , {@code a[@foo > .5]}, and so forth.
 * <p>
 * 
 * @author David F. Houghton - Apr 30, 2012
 * 
 * @param <N>
 */
class AttributeTestPredicate<N> extends Predicate<N> {
	private static final long serialVersionUID = 1L;
	private final AttributeTestExpression<N> a;

	AttributeTestPredicate(Match m, Forester<N> f) {
		a = new AttributeTestExpression<N>(m, f);
	}

	@Override
	Collection<N> filter(Collection<N> c, Index<N> i) {
		List<N> list = new ArrayList<N>(c.size());
		for (N n : c) {
			if (a.test(n, c, i))
				list.add(n);
		}
		return list;
	}

}
