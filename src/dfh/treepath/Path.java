package dfh.treepath;

import java.util.ArrayList;
import java.util.List;

public class Path<N> {
	private final Selector<N>[] selectors;

	Path(Selector<N>[] selectors) {
		this.selectors = selectors;
	}
	
	public List<N> select(N n) {
		List<N> candidates = selectors[0].select(n);
		for (int i = 1; i < selectors.length; i++) {
			List<N> generation = new ArrayList<N>();
			for (N c: candidates)
				generation.addAll(selectors[i].select(c));
			candidates = generation;
		}
		return candidates;
	}
}
