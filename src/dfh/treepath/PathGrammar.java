package dfh.treepath;

import java.util.Set;
import java.util.TreeSet;

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
		/**
		 * E.g., <code>//foo/preceding::*</code>
		 */
		preceding, /**
		 * E.g., <code>//foo/preceding-sibling::*</code>
		 */
		precedingSibling, /**
		 * E.g., <code>//foo/following::*</code>
		 */
		following, /**
		 * E.g., <code>//foo/following-sibling::*</code>
		 */
		followingSibling, /**
		 * E.g., <code>//foo/ancestor::*</code>
		 */
		ancestor, /**
		 * E.g., <code>//foo/ancestor-or-self::*</code>
		 */
		ancestorOrSelf, /**
		 * E.g., <code>//foo/descendant::*</code>
		 */
		descendant, /**
		 * E.g., <code>//foo/descendant-or-self::*</code>
		 */
		descendantOrSelf, /**
		 * E.g., <code>//foo/sibling::*</code>
		 */
		sibling, /**
		 * E.g., <code>//foo/sibling-or-self::*</code>
		 */
		siblingOrSelf, /**
		 * E.g., <code>//foo/leaf::*</code>
		 */
		leaf, /**
		 * E.g., <code>//foo/self::*</code>
		 */
		self, /**
		 * E.g., <code>//foo/parent::*</code>
		 */
		parent, /**
		 * E.g., <code>//foo/child::*</code>
		 */
		child;
		/**
		 * Used in place of {@link #valueOf(String)} to handle hyphens in axis
		 * names.
		 * 
		 * @param s
		 *            axis name
		 * @return axis constant
		 */
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

	/**
	 * The grammar rules.
	 */
	public static String[] rules = {
			//
			"treepath = <path> [ <s> '|' <s> <path> ]*",//
			"path = <first_step> <subsequent_step>*+",//
			"first_step = [{segment} <separator>?+ <step> ]",//
			"id = 'id(' /(?:[^)\\\\]|\\\\.)++/ ')'",//
			"subsequent_step = [{segment} <separator> <step> ]",//
			"separator = /\\/[\\/>]?/",//
			"step = [ <full> | <abbreviated> ] <predicate>*+",//
			"full = <axis>?+ <forward>",//
			"axis = not after [ '//' | '/>' ] <axis_name> '::'", //
			"axis_name = /(?>s(?>ibling(?>-or-self)?+|elf)|p(?>receding(?>-sibling)?+|arent)|leaf|following(?>-sibling)?+|descendant(?>-or-self)?+|child|ancestor(?>-or-self)?+)/",//
			"abbreviated = not after [ '//' | '/>' ] [ '.' | '..' | <id> ]",//
			"forward = <wildcard> | <specific> | <pattern>",//
			"wildcard = '*'",//
			"specific = /[\\p{L}_](?:[\\p{L}\\p{N}_]|[-:](?=[\\p{L}_\\p{N}])|\\\\.)*+/",//
			"pattern = /~(?:[^~\\\\]|\\\\.)++~/",//
			"aname = /@(?:[\\p{L}_$]|\\\\.)(?:[\\p{L}_$\\p{N}]|[-:](?=[\\p{L}_\\p{N}])|\\\\.)*+/",//
			"attribute = <aname> <args>?",//
			"args = '(' <s> <arg> [ <s> ',' <s> <arg> ]* <s> ')'",//
			"arg = <treepath> | <literal> | <num> | <attribute> | <attribute_test> | <condition>",//
			"num = <signed_int> | <float>",//
			"signed_int = /[+-]?+/ <int>",//
			"float = /[+-]?+/ <int>?+ /\\.\\d++/ [ /e[+-]?+/i <int> ]?+",//
			"literal = <squote> | <dquote>",//
			"squote = /'(?:[^']|\\\\.)*+'/",//
			"dquote = /\"(?:[^\"]|\\\\.)*+\"/",//
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
	/**
	 * A reference to the compiled grammar, mostly useful for testing.
	 */
	public static Grammar g = new Grammar(rules);
	static {
		final MatchTest t = new MatchTest() {
			private static final long serialVersionUID = 1L;

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

	/**
	 * No one should every construct an instance of this.
	 */
	private PathGrammar() {
	}

	/**
	 * Calls {@link Grammar#describe()} to print out a more readable list of the
	 * grammar's rules. Condition definitions are not provided but their
	 * semantics can be inferred from their names.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print(g.describe());
	}
}
