package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/*</code>, <code>/*[&#064;foo]</code> and the
 * like, where this is the first expression in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class RootWildcard<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	RootWildcard(Match arguments, Forester<N> f) {
		super(arguments, f);
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		List<N> list = new ArrayList<N>(1);
		list.add(i.root);
		return list;
	}

}
