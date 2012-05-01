package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link Selector} for the . expression.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
class SelfSelector<N> implements Selector<N> {
	@Override
	public Collection<N> select(N n, Index<N> i) {
		List<N> list = new ArrayList<N>(1);
		list.add(n);
		return list;
	}
}
