package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::~foo~ or
 * /preceding::~foo~[&#064;attribute], where this is the first step in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class RootAxisMatching<N> extends AxisSelector<N> {
	private static final long serialVersionUID = 1L;

	protected final NodeTest<N> test;

	RootAxisMatching(String axisName, String pattern, Match arguments,
			Forester<N> f) {
		super(axisName, arguments, f);
		final Pattern p = Pattern.compile(pattern);
		test = new NodeTest<N>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.matchesTag(n, p);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(i.root, axis, test, i);
	}

}
