package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;

/**
 * For implementing predicates like <code>a[&#064;foo = 1]</code>,
 * <code>a[&#064;foo = 'bar']</code>, <code>a[&#064;foo &gt; .5]</code>, and so forth.
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
