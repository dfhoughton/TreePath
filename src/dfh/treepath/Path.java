/*
 * dfh.treepath -- a library for querying arbitrary trees
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class Path<N> {
	private final Selector<N>[][] selectors;
	private final Forester<N> f;

	Path(Forester<N> f, Selector<N>[][] selectors) {
		this.f = f;
		this.selectors = selectors;
	}

	public Collection<N> select(N n) {
		Index<N> index = f.treeIndex(n);
		List<N> initialList = new ArrayList<N>(1);
		initialList.add(n);
		Collection<N> selection = new LinkedHashSet<N>();
		for (Selector<N>[] alternate : selectors) {
			Collection<N> candidates = alternate[0].select(initialList, index);
			for (int i = 1; i < alternate.length; i++)
				candidates = alternate[i].select(candidates, index);
			selection.addAll(candidates);
		}
		return selection;
	}
}
