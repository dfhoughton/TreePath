package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;

/**
 * Base {@link Selector} class for steps involving the wildcard expression
 * <code>*</code>.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public abstract class WildcardSelector<N> implements Selector<N> {

	protected NodeTest<N> test;

	@SuppressWarnings("unchecked")
	public WildcardSelector(Match arguments, Forester<N> f) {
		List<Match> argList = arguments.closest(TestSelector.argMT);
		final Predicate<N>[] predicates = new Predicate[argList.size()];
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = Predicate.build(argList.get(i), f);
			if (predicates[i] instanceof IndexPredicate)
				throw new PathException(
						"index predicates cannot be used with the wildcard character");
		}
		test = new NodeTest<N>() {
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
