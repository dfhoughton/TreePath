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
			"treepath = <path> [ '|' <path> ]*",//
			"path = <step>++",//
			"step = <forward> <predicate>*+",//
			"forward = '/'{,2} <name> | '.'{1,2}",//
			"name = <wildcard> | <specific> | <pattern>",//
			"wildcard = '*'",//
			"specific = /(?:\\w|\\\\.)++/",//
			"pattern = /~(?:[^~\\\\]|\\\\.)++~/ (compiles)",//
			"aname = /@[\\p{L}_$][\\p{L}_$\\p{N}]*+/",//
			"attribute = <aname> <args>?",//
			"args = '(' <s> <arg> [{tail} [ <s> ',' <s> <arg> ]* ] <s> ')'",//
			"arg = <treepath> | <literal>",//
			"literal = <squote> | <dquote>",//
			"squote = /'(?:[^']|\\\\.)++'/",//
			"dquote = /\"(?:[^\"]|\\\\.)++\"/",//
			"predicate = '[' <s> [ <treepath> | <condition> ] <s> ']'",//
			"s = /\\s*+/",//
			"condition = <attribute> | <not_cnd> | <or_cnd> | <and_cnd> | <xor_cnd> | <group>",//
			"group = '(' <s> <condition> <s> ')'",//
			"not_cnd = /!|not/ <s> <condition>",//
			"or_cnd = <condition> [ <s> /\\||or/ <s> <condition> ]+",//
			"and_cnd = <condition> [ <s> /&|and/ <s> <condition> ]+",//
			"xor_cnd = <condition> [ <s> /^|xor/ <s> <condition> ]+",//
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
	
	public static void main(String[] args) {
		System.out.print(g.describe());
	}
}
