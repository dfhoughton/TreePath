package dfh.treepath;

import java.util.Collection;

interface Selector<N> {
	/**
	 * @param n
	 * @param i
	 * @return the set of nodes passing this selector's condition
	 */
	Collection<N> select(Collection<N> n, Index<N> i);
}
