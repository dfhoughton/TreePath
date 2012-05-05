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
	private final HashMap<N, N> parentIndex;

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
		parentIndex = new HashMap<N, N>();
	}

	protected void index(N n, N c) {
		parentIndex.put(c, n);
	}

	/**
	 * Returns the parent of a node.
	 * 
	 * @param n
	 *            a node
	 * @return the parent of n
	 */
	public N parent(N n) {
		return parentIndex.get(n);
	}
}
