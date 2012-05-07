package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dfh.grammar.Match;

/**
 * {@link Selector} for the .. expression.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class ParentSelector<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	public ParentSelector(Match predicates, Forester<N> f) {
		super(predicates, f);
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		if (i.isRoot(n))
			return Collections.emptyList();
		List<N> list = new ArrayList<N>(1);
		list.add(i.f.parent(n, i));
		return list;
	}
}
