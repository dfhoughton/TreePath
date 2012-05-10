/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that caches information pertaining to a particular tree, if
 * necessary. For some sorts of tree, this class will do little more than
 * provide a reference back to the {@link Forester} and root node. For others
 * the index supplements the tree with information not accessible from
 * particular nodes.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 *            a type of tree node
 */
public class Index<N> {
	/**
	 * root node in tree
	 */
	public final N root;
	/**
	 * {@link Forester} capable of interpreting the tree, generally the one that
	 * created the index.
	 */
	public final Forester<N> f;
	/**
	 * Map from ids to nodes, if this is appropriate for the tree type and any
	 * identified nodes exist.
	 */
	protected Map<String, N> identifiedNodes;
	/**
	 * Whether the index has been initialized.
	 */
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

	/**
	 * Walk the tree, indexing all nodes.
	 * 
	 * @param n
	 *            current node
	 */
	protected void walk(N n) {
		List<N> children = f.kids(n, this);
		index(n);
		for (N c : children) {
			index(n, c);
			walk(c);
		}
	}

	/**
	 * Record any unique identifier of this node.
	 * 
	 * @param n
	 *            node
	 */
	protected void index(N n) {
		String id = id(n);
		if (id != null)
			identifiedNodes.put(id, n);
	}

	/**
	 * Do relevant indexing for a parent and child pair. Unless overridden this
	 * method does nothing.
	 * 
	 * @param n
	 *            parent node
	 * @param c
	 *            child node
	 */
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
	 * Returns the unique identifier, if any, that identifies this node.
	 * 
	 * @param n
	 *            node
	 * @return a unique string identifier of this node, if any
	 */
	public String id(N n) {
		return null;
	}
}
