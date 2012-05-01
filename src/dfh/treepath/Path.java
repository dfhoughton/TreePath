/*
 * dfh.treepath -- a library for querying arbitrary trees
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Path<N> {
	private final Selector<N>[][] selectors;
	private final Forester<N> f;

	Path(Forester<N> f, Selector<N>[][] selectors) {
		this.f = f;
		this.selectors = selectors;
	}

	public Collection<N> select(N root) {
		Index<N> i = f.treeIndex(root);
		if (f.isRoot(root, null, i))
			return select(root, i);
		throw new PathException(
				"select can only be called with the root node of a tree");
	}

	Collection<N> select(N n, Index<N> index) {
		Set<N> selection = new LinkedHashSet<N>();
		for (Selector<N>[] fork : selectors) {
			selection.addAll(select(n, index, fork, 0));
		}
		return selection;
	}

	Collection<N> select(N n, Index<N> index, Selector<N>[] fork, int stepIndex) {
		Collection<N> next = fork[stepIndex++].select(n, index);
		if (stepIndex == fork.length)
			return next;
		Set<N> selection = new LinkedHashSet<N>();
		for (N c : next) {
			selection.addAll(select(c, index, fork, stepIndex));
		}
		return selection;
	}
}
