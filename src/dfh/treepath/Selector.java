package dfh.treepath;

import java.util.Collection;

interface Selector<N> {
	/**
	 * @param n
	 * @return the set of nodes passing this selector's condition
	 */
	Collection<N> select(Collection<N> n);
}
