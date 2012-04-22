package dfh.treepath;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import dfh.grammar.Condition;
import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;

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
			"path = <relative_path> | <absolute_path>",//
			"absolute_path = '/' <relative_path>? | '//' <relative_path>",//
			"relative_path = <step> [ <step_separator> <step> ]*+",//
			"step_separator = '/'{1,2}",//
			"abbreviated_path = <relative_path> '//' <step>",//
			"step = <full> | <abbreviated>",//
			"full = <axis>?+ <forward> <predicate>*+",//
			"axis = not after '//' <axis_name> '::'", //
			"axis_name = /(?>preceding(?>-sibling)?+|following(?>-sibling)?+|descendant(?>-or-self)?+|ancestor(?>-or-self)?+)/",//
			"abbreviated = '.' | '..'",//
			"forward = <name> | '.'{1,2}",//
			"name = <wildcard> | <specific> | <pattern>",//
			"wildcard = '*'",//
			"specific = /[\\p{L}_](?:[\\p{L}\\p{N}_]|\\\\.)*+/",//
			"pattern = /~(?:[^~\\\\]|\\\\.)++~/ (compiles)",//
			"aname = /@[\\p{L}_$][\\p{L}_$\\p{N}]*+/",//
			"attribute = <aname> <args>?",//
			"args = '(' <s> <arg> [ <s> ',' <s> <arg> ]* <s> ')'",//
			"arg = <treepath> | <literal> | <num> | <attribute>",//
			"num = <signed_int> | <float>",//
			"signed_int = /[+-]?+/ <int>",//
			"float = /[+-]/?+ <int>?+ /\\.\\d++/ [ /e[+-]?+/i <int> ]?+",//
			"literal = <squote> | <dquote>",//
			"squote = /'(?:[^']|\\\\.)++'/",//
			"dquote = /\"(?:[^\"]|\\\\.)++\"/",//
			"predicate = '[' <s> [ <int> | <treepath> | <condition> ] <s> ']'",//
			"int = /\\b(?:0|[1-9][0-9]*+)\\b/",//
			"s = /\\s*+/",//
			"condition = <term> | <not_cnd> | <or_cnd> | <and_cnd> | <xor_cnd> | <group>",//
			"term = <attribute> | <path>",//
			"group = '(' <s> <condition> <s> ')'",//
			"not_cnd = '!' <s> <condition> (not_precedence)",//
			"or_cnd = <condition> [ <s> '||' <s> <condition> ]+",//
			"and_cnd = <condition> [ <s> '&&' <s> <condition> ]+ (and_precedence)",//
			"xor_cnd = <condition> [ <s> '^' <s> <condition> ]+ (xor_precedence)",//
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
		final MatchTest t = new MatchTest() {
			@Override
			public boolean test(Match m) {
				return m.rule().label().id.equals("condition");
			}
		};
		g.defineCondition("not_precedence", new Condition() {
			Set<String> good = new TreeSet<String>();
			{
				good.add("not_cnd");
				good.add("term");
				good.add("group");
			}

			@Override
			public boolean passes(Match n, CharSequence s) {
				for (Match m : n.closest(t)) {
					if (!good.contains(m.children()[0].rule().label().id))
						return false;
				}
				return true;
			}
		});
		g.defineCondition("and_precedence", new Condition() {
			Set<String> good = new TreeSet<String>();
			{
				good.add("and_cnd");
				good.add("not_cnd");
				good.add("term");
				good.add("group");
			}

			@Override
			public boolean passes(Match n, CharSequence s) {
				for (Match m : n.closest(t)) {
					if (!good.contains(m.children()[0].rule().label().id))
						return false;
				}
				return true;
			}
		});
		g.defineCondition("xor_precedence", new Condition() {
			Set<String> good = new TreeSet<String>();
			{
				good.add("xor_cnd");
				good.add("and_cnd");
				good.add("not_cnd");
				good.add("term");
				good.add("group");
			}

			@Override
			public boolean passes(Match n, CharSequence s) {
				for (Match m : n.closest(t)) {
					if (!good.contains(m.children()[0].rule().label().id))
						return false;
				}
				return true;
			}
		});
	}

	public static void main(String[] args) {
		System.out.print(g.describe());
	}
}
