package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::foo or
 * /preceding::foo[@attribute], where this is the first step in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class RootAxisTag<N> extends AxisSelector<N> {

	protected final NodeTest<N> test;

	public RootAxisTag(String axisName, final String tag, Match arguments,
			Forester<N> f) {
		super(axisName, arguments, f);
		test = new NodeTest<N>() {
			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.hasTag(n, tag);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(i.root, axis, test, i);
	}

}
