package dfh.treepath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	protected Map<String, N> identifiedNodes;
	protected boolean indexed;

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
		identifiedNodes = new HashMap<String, N>();
	}

	/**
	 * Walks tree performing indexing.
	 */
	protected synchronized void index() {
		walk(root);
		indexed = true;
	}

	/**
	 * Returns whether {@link #index()} has been called. This is used to prevent
	 * redundant tree walking.
	 * 
	 * @return whether {@link #index()} has been called
	 */
	protected boolean indexed() {
		return indexed;
	}

	protected void walk(N n) {
		List<N> children = f.kids(n, this);
		index(n);
		for (N c : children) {
			index(n, c);
			walk(c);
		}
	}

	protected void index(N n) {
		String id = id(n);
		if (id != null)
			identifiedNodes.put(id, n);
	}

	protected void index(N n, N c) {
	}

	/**
	 * Indicates whether the given node is the root.
	 * 
	 * @param n
	 * @return whether the given node is the root of the tree
	 */
	public boolean isRoot(N n) {
		boolean isRoot = n == root;
		return isRoot;
	}

	/**
	 * @param n
	 * @return a unique string identifier of this node, if any
	 */
	public String id(N n) {
		return null;
	}
}
