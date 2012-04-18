package dfh.treepath;

import dfh.grammar.Grammar;

/**
 * Grammar for parsing path expressions.
 * <p>
 * 
 * @author David F. Houghton - Apr 18, 2012
 * 
 */
public class PathGrammar {
	public static String[] rules = {
		//
		"path = <step>++",//
	};
	public static Grammar g = new Grammar(rules);
}
