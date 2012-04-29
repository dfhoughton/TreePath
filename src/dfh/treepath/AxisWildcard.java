package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /preceding::* or /preceding::*[@attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class AxisWildcard<N> extends AxisSelector<N> {

	public AxisWildcard(String axisName, Match arguments, Forester<N> f) {
		super(axisName, arguments, f);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.axis(n, axis, (NodeTest<N>) TrueTest.test(), i);
	}

}
