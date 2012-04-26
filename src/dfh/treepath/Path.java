package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class Path<N> {
	private final Selector<N>[][] selectors;

	Path(Selector<N>[][] selectors) {
		this.selectors = selectors;
	}

	public Collection<N> select(N n) {
		List<N> initialList = new ArrayList<N>(1);
		initialList.add(n);
		Collection<N> selection = new LinkedHashSet<N>();
		for (Selector<N>[] alternate : selectors) {
			Collection<N> candidates = alternate[0].select(initialList);
			for (int i = 1; i < alternate.length; i++)
				candidates = alternate[i].select(candidates);
			selection.addAll(candidates);
		}
		return selection;
	}
}
