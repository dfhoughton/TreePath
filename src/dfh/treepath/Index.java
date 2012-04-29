package dfh.treepath;

/**
 * A class that caches information pertaining to a particular tree, if
 * necessary.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 *            a type of tree node
 */
public class Index<N> {
	public final N root;
	public final Forester<N> f;

	/**
	 * Constructs an index for the tree with the specified root.
	 * 
	 * @param root
	 *            the root of a tree
	 * @param f
	 *            a {@link Forester} that can query nodes in the tree
	 */
	public Index(N root, Forester<N> f) {
		this.root = root;
		this.f = f;
	}

	/**
	 * Indicates whether the given node is the root.
	 * 
	 * @param n
	 * @return whether the given node is the root of the tree
	 */
	public boolean isRoot(N n) {
		return n == root;
	}
}
