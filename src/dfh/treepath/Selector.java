package dfh.treepath;

import java.io.Serializable;
import java.util.Collection;

interface Selector<N> extends Serializable {
	/**
	 * @param n
	 * @param i
	 * @return the set of nodes passing this selector's condition
	 */
	Collection<N> select(N n, Index<N> i);
}
