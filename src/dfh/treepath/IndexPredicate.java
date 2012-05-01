package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Instantiates the predicate in <code>a[1]</code> and the like. This predicate
 * returns the
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 */
class IndexPredicate<N> extends Predicate<N> {

	private int index;

	IndexPredicate(int index) {
		this.index = index;
	}

	@Override
	Collection<N> filter(Collection<N> c, Index<N> i) {
		if (index >= c.size())
			return Collections.emptyList();
		List<N> filtrate = new ArrayList<N>(1);
		int j = 0;
		for (Iterator<N> k = c.iterator(); k.hasNext();) {
			N n = k.next();
			if (j++ == index) {
				filtrate.add(n);
				break;
			}
		}
		return filtrate;
	}

}
