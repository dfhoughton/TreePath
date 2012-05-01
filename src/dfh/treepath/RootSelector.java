package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RootSelector<N> implements Selector<N> {

	@Override
	public Collection<N> select(N n, Index<N> i) {
		List<N> list = new ArrayList<N>(1);
		list.add(i.root);
		return list;
	}

}
