package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/*</code>, <code>/*[@foo]</code> and the
 * like, where this is the first expression in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class RootWildcard<N> extends TestSelector<N> {

	public RootWildcard(Match arguments, Forester<N> f) {
		super(arguments, f);
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.children(i.root, i);
	}

}
