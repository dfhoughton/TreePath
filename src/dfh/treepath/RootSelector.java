package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;

class RootSelector<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	public RootSelector(Match predicates, Forester<N> f) {
		super(predicates, f);
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		List<N> list = new ArrayList<N>(1);
		list.add(i.root);
		return list;
	}

}
