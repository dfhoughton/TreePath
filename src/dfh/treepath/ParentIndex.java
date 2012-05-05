package dfh.treepath;

import java.util.HashMap;

/**
 * An {@link Index} that caches the parents of nodes. This is useful for trees
 * that don't provide backwards links from child to parent.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 *            a type of tree node
 */
public class ParentIndex<N> extends Index<N> {
	private final HashMap<N, N> index;

	/**
	 * Constructs a {@link ParentIndex} for the given tree.
	 * 
	 * @param n
	 *            the root node of a tree
	 * @param f
	 *            a {@link Forester} that can be used to query the nodes of the
	 *            tree
	 */
	public ParentIndex(N n, Forester<N> f) {
		super(n, f);
		index = new HashMap<N, N>();
		addChildren(n);
	}

	private void addChildren(N n) {
		for (N c : f.kids(n, this)) {
			index.put(c, n);
			addChildren(c);
		}
	}

	/**
	 * Returns the parent of a node.
	 * 
	 * @param n
	 *            a node
	 * @return the parent of n
	 */
	public N parent(N n) {
		return index.get(n);
	}
}
