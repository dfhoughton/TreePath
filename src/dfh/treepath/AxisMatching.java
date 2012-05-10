package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::~foo~ or
 * /preceding::~foo~[&#064;attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AxisMatching<N> extends AxisSelector<N> {
	private static final long serialVersionUID = 1L;

	protected final NodeTest<N> test;

	AxisMatching(String axisName, String pattern, Match arguments, Forester<N> f) {
		super(axisName, arguments, f);
		try {
			final Pattern p = Pattern.compile(pattern);
			test = new NodeTest<N>() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean passes(N n, Index<N> i) {
					return i.f.matchesTag(n, p);
				}
			};
		} catch (PatternSyntaxException e) {
			throw new PathException("could not compile " + pattern
					+ " as a regular expression", e);
		}
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, axis, test, i);
	}

}
