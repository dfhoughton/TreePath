package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Implements expressions such as /* or /*[foo]
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class ChildWildcard<N> extends WildcardSelector<N> {
	private static final long serialVersionUID = 1L;

	public ChildWildcard(Match arguments, Forester<N> f) {
		super(arguments, f);
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		return i.f.children(n, test, i);
	}

}
