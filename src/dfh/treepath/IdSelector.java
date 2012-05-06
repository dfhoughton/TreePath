package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dfh.grammar.Match;

/**
 * Implements id(foo) selector.
 * <p>
 * 
 * @author David F. Houghton - May 5, 2012
 * 
 * @param <N>
 */
class IdSelector<N> implements Selector<N> {
	private static final long serialVersionUID = 1L;
	private final String id;

	public IdSelector(Match fs) {
		id = fs.children()[0].children()[1].group().replaceAll("\\\\(.)", "$1");
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		N identified = i.identifiedNodes.get(id);
		if (identified == null)
			return Collections.emptyList();
		List<N> list = new ArrayList<N>(1);
		list.add(identified);
		return list;
	}

}
