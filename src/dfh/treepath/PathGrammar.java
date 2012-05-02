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
	/**
	 * Axes known to tree path grammar.
	 * <p>
	 * 
	 * @author David F. Houghton - Apr 23, 2012
	 * 
	 */
	public static enum Axis {
		preceding, precedingSibling, following, followingSibling, ancestor, ancestorOrSelf, descendant, descendantOrSelf, sibling, siblingOrSelf, leaf, self, parent, child;
		public static Axis vo(String s) {
			if (s.indexOf('-') == -1)
				return valueOf(s);
			if ("preceding-sibling".equals(s))
				return precedingSibling;
			if ("following-sibling".equals(s))
				return followingSibling;
			if ("ancestor-or-self".equals(s))
				return ancestorOrSelf;
			if ("descendant-or-self".equals(s))
				return descendantOrSelf;
			if ("sibling-or-self".equals(s))
				return siblingOrSelf;
			return null;
		}
	}

	public static String[] rules = {
			//
			"treepath = <path> [ '|' <path> ]*",//
			"path = <first_step> <subsequent_step>*+",//
			"first_step = [{segment} <separator>?+ <step> ]",//
			"subsequent_step = [{segment} <separator> <step> ]",//
			"separator = /\\/[\\/>]?/",//
			"step = <full> | <abbreviated>",//
			"full = <axis>?+ <forward> <predicate>*+",//
			"axis = not after [ '//' | '/>' ] <axis_name> '::'", //
			"axis_name = /(?>s(?>ibling(?>-or-self)?+|elf)|p(?>receding(?>-sibling)?+|arent)|leaf|following(?>-sibling)?+|descendant(?>-or-self)?+|child|ancestor(?>-or-self)?+)/",//
			"abbreviated = '.' | '..'",//
			"forward = <wildcard> | <specific> | <pattern>",//
			"wildcard = '*'",//
			"specific = /[\\p{L}_](?:[\\p{L}\\p{N}_]|[-:](?=[\\p{L}_\\p{N}])|\\\\.)*+/",//
			"pattern = /~(?:[^~\\\\]|\\\\~)++~/ (compiles)",//
			"aname = /@(?:[\\p{L}_$]|\\\\.)(?:[\\p{L}_$\\p{N}]|[-:](?=[\\p{L}_\\p{N}])|\\\\.)*+/",//
			"attribute = <aname> <args>?",//
			"args = '(' <s> <arg> [ <s> ',' <s> <arg> ]* <s> ')'",//
			"arg = <treepath> | <literal> | <num> | <attribute>",//
			"num = <signed_int> | <float>",//
			"signed_int = /[+-]?+/ <int>",//
			"float = /[+-]?+/ <int>?+ /\\.\\d++/ [ /e[+-]?+/i <int> ]?+",//
			"literal = <squote> | <dquote>",//
			"squote = /'(?:[^']|\\\\.)++'/",//
			"dquote = /\"(?:[^\"]|\\\\.)++\"/",//
			"predicate = '[' <s> [ <signed_int> | <treepath> | <attribute_test> | <condition> ] <s> ']'",//
			"int = /\\b(?:0|[1-9][0-9]*+)\\b/",//
			"s = /\\s*+/",//
			"condition = <term> | <not_cnd> | <or_cnd> | <and_cnd> | <xor_cnd> | <group>",//
			"term = <attribute> | <attribute_test> | <treepath>",//
			"attribute_test = <attribute> <s> <cmp> <s> <value> | <value> <s> <cmp> <s> <attribute>",//
			"cmp = /[<>=]=?|!=/",//
			"value = <literal> | <num> | <attribute>",//
			"group = '(' <s> <condition> <s> ')'",//
			"not_cnd = /!|(?<!\\/)\\bnot\\b(?!\\/)/ <s> <condition> (not_precedence)",//
			"or_cnd = <condition> [ <s> /\\|{2}|(?<!\\/)\\bor\\b(?!\\/)/ <s> <condition> ]+",//
			"and_cnd = <condition> [ <s> /&|(?<!\\/)\\band\\b(?!\\/)/ <s> <condition> ]+ (and_precedence)",//
			"xor_cnd = <condition> [ <s> /^|(?<!\\/)\\bxor\\b(?!\\/)/ <s> <condition> ]+ (xor_precedence)",//
	};
	public static Grammar g = new Grammar(rules);
	static {
		g.defineCondition("compiles", new Condition() {
			@Override
			public boolean passes(CharSequence s) {
				try {
					String p = s.subSequence(1, s.length() - 1).toString()
							.replaceAll("\\\\~", "~");
					Pattern.compile(p);
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
