package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

/**
 * {@link Selector} implementing <code>//foo</code> and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AnywhereTag<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	AnywhereTag(final String tag, Match arguments, Forester<N> f) {
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
		return i.f.axis(n, Axis.descendantOrSelf, test, i);
	}

}
