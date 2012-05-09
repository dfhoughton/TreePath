package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::* or /preceding::*[&#064;attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AxisWildcard<N> extends AxisSelector<N> {
	private static final long serialVersionUID = 1L;

	AxisWildcard(String axisName, Match arguments, Forester<N> f) {
		super(axisName, arguments, f);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, axis, (NodeTest<N>) TrueTest.test(), i);
	}

}
