package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/&gt;foo</code> and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class ClosestTag<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	ClosestTag(final String tag, Match arguments, Forester<N> f) {
		super(arguments, f);
		test = new NodeTest<N>() {
			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.hasTag(n, tag);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.closest(n, test, i);
	}

}
