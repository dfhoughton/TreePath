/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

abstract class AxisSelector<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	protected final Axis axis;

	AxisSelector(String axisName, Match predicates, Forester<N> f) {
		super(predicates, f);
		axis = Axis.vo(axisName);
	}
}
