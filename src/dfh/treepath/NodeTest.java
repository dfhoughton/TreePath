/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.io.Serializable;

/**
 * Tests to see whether the given node has some condition.
 * <p>
 * 
 * @author David F. Houghton - May 5, 2012
 * 
 * @param <N>
 *            node type
 */
public interface NodeTest<N> extends Serializable {
	/**
	 * Tests the node
	 * 
	 * @param n
	 *            context node
	 * @param i
	 *            tree index
	 * @return whether the node passes the test
	 */
	boolean passes(N n, Index<N> i);
}
