package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::~foo~ or
 * /preceding::~foo~[@attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class AxisMatching<N> extends AxisSelector<N> {

	protected final NodeTest<N> test;

	public AxisMatching(String axisName, String pattern, Match arguments,
			Forester<N> f) {
		super(axisName, arguments, f);
		final Pattern p = Pattern.compile(pattern);
		test = new NodeTest<N>() {
			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.matchesTag(n, p);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, axis, test, i);
	}

}
