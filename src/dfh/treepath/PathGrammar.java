package dfh.treepath;

import java.util.regex.Pattern;

import dfh.grammar.Condition;
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
			"ROOT = <path> [ '|' <path> ]*",//
			"path = <step>++",//
			"step = <forward> <predicate>*+",//
			"forward = '/'{,2} <name> | '.'{1,2}",//
			"name = <wildcard> | <specific> | <pattern>",//
			"wildcard = '*'",//
			"specific = /(?:\\w|\\\\.)++/",//
			"pattern = /~(?:[^~\\\\]|\\\\.)++~/ (compiles)",//
			"attribute = /@[\\p{L}_$][\\p{L}_$\\p{N}]*+/",//
	};
	public static Grammar g = new Grammar(rules);
	static {
		g.defineCondition("compiles", new Condition() {
			@Override
			public boolean passes(CharSequence s) {
				try {
					Pattern.compile(s.subSequence(1, s.length() - 1).toString());
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
}
