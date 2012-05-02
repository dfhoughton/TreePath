package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link Selector} for the .. expression.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class ParentSelector<N> implements Selector<N> {
	private static final long serialVersionUID = 1L;

	@Override
	public Collection<N> select(N n, Index<N> i) {
		if (i.isRoot(n))
			return Collections.emptyList();
		List<N> list = new ArrayList<N>(1);
		list.add(i.f.parent(n, i));
		return list;
	}
}
