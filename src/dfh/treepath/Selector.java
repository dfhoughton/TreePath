package dfh.treepath;

import java.util.List;

interface Selector<N> {
	/**
	 * @param n
	 * @return the set of nodes passing this selector's condition
	 */
	List<N> select(N n);
}
