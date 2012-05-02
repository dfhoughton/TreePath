package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as />*[foo]
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class ClosestWildcard<N> extends WildcardSelector<N> {
	private static final long serialVersionUID = 1L;

	public ClosestWildcard(Match arguments, Forester<N> f) {
		super(arguments, f);
		if (arguments.length() == 0)
			throw new PathException("/>* must be followed by a predicate");
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		return i.f.closest(n, test, i);
	}

}
