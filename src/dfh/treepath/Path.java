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
		Collection<N> selection = new LinkedHashSet<N>();
		for (Selector<N>[] alternate : selectors) {
			List<N> candidates = alternate[0].select(n);
			for (int i = 1; i < alternate.length; i++) {
				List<N> generation = new ArrayList<N>();
				for (N c : candidates)
					generation.addAll(alternate[i].select(c));
				candidates = generation;
			}
			selection.addAll(candidates);
		}
		return selection;
	}
}
