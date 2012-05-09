package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::foo or
 * /preceding::foo[&#064;attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AxisTag<N> extends AxisSelector<N> {
	private static final long serialVersionUID = 1L;

	protected final NodeTest<N> test;

	AxisTag(String axisName, final String tag, Match predicates, Forester<N> f) {
		super(axisName, predicates, f);
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
		return i.f.axis(n, axis, test, i);
	}

}
