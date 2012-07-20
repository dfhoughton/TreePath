package dfh.treepath.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import dfh.grammar.Condition;
import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.treepath.PathGrammar;

/**
 * Tests to confirm the validity of the operator precedence mechanism employed
 * by {@link PathGrammar}.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 */
public class PrecedenceTest {
	private static Grammar g;

	@SuppressWarnings("serial")
	@BeforeClass
	public static void makeGrammar() {
		String[] rules = {
				//
				"cnd = <term> | <or_cnd> | <not_cnd> | <and_cnd> | <xor_cnd> | <group>",//
				"term = /\\b\\w\\b/",//
				"s = /\\s*+/",//
				"not_cnd = 'not' <s> <cnd> (not_precedence)",//
				"or_cnd = <cnd> [ <s> 'or' <s> <cnd> ]+",//
				"group = '(' <s> <cnd> <s> ')'",//
				"and_cnd = <cnd> [ <s> 'and' <s> <cnd> ]+ (and_precedence)",//
				"xor_cnd = <cnd> [ <s> 'xor' <s> <cnd> ]+ (xor_precedence)",//
		};
		g = new Grammar(rules);
		final MatchTest t = new MatchTest() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Match m) {
				return m.rule().label().id.equals("cnd");
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
				List<Match> conditions = n.closest(t);
				for (Match m : conditions) {
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
				List<Match> conditions = n.closest(t);
				for (Match m : conditions) {
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
				List<Match> conditions = n.closest(t);
				for (Match m : conditions) {
					if (!good.contains(m.children()[0].rule().label().id))
						return false;
				}
				return true;
			}
		});
	}

	@Test
	public void and1() {
		Match m = g.matches("a and b and c").match();
		m = m.first("and_cnd");
		assertNotNull(m);
		assertEquals("a and b and c", m.group());
	}

	@Test
	public void and2() {
		Match m = g.matches("a and b or c").match();
		m = m.first("and_cnd");
		assertNotNull(m);
		assertEquals("a and b", m.group());
	}

	@Test
	public void and3() {
		Match m = g.matches("a or b and c").match();
		m = m.first("and_cnd");
		assertNotNull(m);
		assertEquals("b and c", m.group());
	}

	@Test
	public void and4() {
		Match m = g.matches("(a or b) and c").match();
		m = m.first("and_cnd");
		assertNotNull(m);
		assertEquals("(a or b) and c", m.group());
	}

	@Test
	public void complex1() {
		Match n = g.matches("a and not b or c").match();
		Match m = n.first("and_cnd");
		assertNotNull(m);
		assertEquals("a and not b", m.group());
		m = n.first("not_cnd");
		assertNotNull(m);
		assertEquals("not b", m.group());
		m = n.first("or_cnd");
		assertNotNull(m);
		assertEquals("a and not b or c", m.group());
	}

	@Test
	public void complex2() {
		Match n = g.matches("not a or b and c").match();
		Match m = n.first("and_cnd");
		assertNotNull(m);
		assertEquals("b and c", m.group());
		m = n.first("not_cnd");
		assertNotNull(m);
		assertEquals("not a", m.group());
		m = n.first("or_cnd");
		assertNotNull(m);
		assertEquals("not a or b and c", m.group());
	}
}
