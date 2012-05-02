package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

/**
 * Implements expressions such as //* or //*[foo]
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class AnywhereWildcard<N> extends WildcardSelector<N> {
	private static final long serialVersionUID = 1L;

	AnywhereWildcard(Match arguments, Forester<N> f) {
		super(arguments, f);
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		return i.f.axis(n, Axis.descendantOrSelf, test, i);
	}

}
