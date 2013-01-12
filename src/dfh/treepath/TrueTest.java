/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

/**
 * {@link NodeTest} that always returns {@code true}. Implements wildcard
 * expression -- "*".
 * <p>
 * 
 * @author David F. Houghton - May 8, 2012
 * 
 * @param <N>
 *            node type
 */
public class TrueTest<N> implements NodeTest<N> {
	private static final long serialVersionUID = 1L;

	private TrueTest() {
	}

	@SuppressWarnings("rawtypes")
	private static final TrueTest<?> test = new TrueTest();

	@SuppressWarnings("unchecked")
	public static final <N> TrueTest<N> test() {
		return (TrueTest<N>) test;
	}

	@Override
	public boolean passes(N n, Index<N> i) {
		return true;
	}

}
