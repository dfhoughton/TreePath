/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import dfh.grammar.Assertion;
import dfh.grammar.BacktrackingBarrier;
import dfh.grammar.HiddenSpace;
import dfh.grammar.Label.Type;
import dfh.grammar.Match;
import dfh.grammar.Rule;
import dfh.grammar.VisibleSpace;

/**
 * A {@link Forester} adaptor for {@link Match} trees.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 */
public class MatchPath extends FunctionalForester<Match> {
	private static final long serialVersionUID = 1L;
	/**
	 * {@link NodeTest} matching all zero-width nodes in the parse tree. This
	 * will exclude empty quantified lists, assertions, zero-width regular
	 * expressions, and so on.
	 */
	public static final NodeTest<Match> zeroNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return n.length() == 0;
		}
	};
	/**
	 * {@link NodeTest} matching nodes whose group contains only whitespace.
	 */
	public static final NodeTest<Match> whitespaceNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;
		private Pattern p = Pattern.compile("\\s++");

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return p.matcher(n.group()).matches();
		}
	};
	/**
	 * {@link NodeTest} matching nodes generated by backtracking barriers. This
	 * is a subset of zero-width nodes.
	 */
	public static final NodeTest<Match> barrierNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return n.rule() instanceof BacktrackingBarrier;
		}
	};
	/**
	 * {@link NodeTest} matching nodes generated by assertions. This is a subset
	 * of zero-width nodes. Note, this does not include regex assertions like
	 * (?=sue); these are only nodes generated by {@link Assertion} rules.
	 */
	public static final NodeTest<Match> assertionNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return n.rule() instanceof Assertion;
		}
	};
	/**
	 * {@link NodeTest} matching nodes generated by '.' in rules such as
	 * 
	 * <pre>
	 * rule := [ 'la' . ]+ 'di' 'da'
	 * </pre>
	 * 
	 * This is a subset whitespace nodes. This test will match faster than
	 * {@link #whitespaceNT} because it doesn't require pattern matching.
	 */
	public static final NodeTest<Match> visibleSpaceNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return n.rule().label().equals(VisibleSpace.LABEL);
		}
	};
	/**
	 * {@link NodeTest} matches whitespace nodes in rules such as
	 * 
	 * <pre>
	 * rule := 'la' 'di' 'da'
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 * rule .= 'la' 'di' 'da'
	 * </pre>
	 * 
	 * This is a subset whitespace nodes. This test will match faster than
	 * {@link #whitespaceNT} because it doesn't require pattern matching.
	 */
	public static final NodeTest<Match> hiddenSpaceNT = new NodeTest<Match>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean passes(Match n, Index<Match> i) {
			return n.rule().label().equals(HiddenSpace.LABEL);
		}
	};
	@SuppressWarnings("unchecked")
	private static final MatchPath standard = new MatchPath(visibleSpaceNT,
			hiddenSpaceNT, zeroNT, whitespaceNT);

	/**
	 * Delegates to {@link FunctionalForester#FunctionalForester(NodeTest...)}.
	 * 
	 * @param ignore
	 *            node types to ignore
	 */
	public MatchPath(NodeTest<Match>... ignore) {
		super(ignore);
	}

	/**
	 * Returns as a singleton instance a {@link MatchPath} {@link Forester} that
	 * will ignore all zero-width or whitespace nodes. This forester uses the
	 * {@link #visibleSpaceNT}, {@link #hiddenSpaceNT}, {@link #zeroNT}, and
	 * {@link #whitespaceNT} node tests to determine which nodes are ignorable.
	 * This gets rid of nodes generated by assertions, backtracking barriers,
	 * repeating patterns with zero repetitions, empty string literals,
	 * zero-width regular expressions -- more or less everything you likely want
	 * to ignore anyway.
	 * 
	 * @return {@link MatchPath} forester that ignores zero-width and whitespace
	 *         nodes
	 */
	public static MatchPath standard() {
		return standard;
	}

	/**
	 * Returns {@link Match#children()} as a list.
	 */
	@Override
	protected List<Match> children(Match n, Index<Match> i) {
		List<Match> children = new ArrayList<Match>(n.children().length);
		for (Match m : n.children())
			children.add(m);
		return children;
	}

	/**
	 * Calls {@link Match#hasLabel(String)}.
	 */
	@Override
	protected boolean hasTag(Match n, String tag) {
		return n.hasLabel(tag);
	}

	/**
	 * Calls {@link Match#hasLabel(Pattern)}.
	 */
	@Override
	protected boolean matchesTag(Match n, Pattern p) {
		return n.hasLabel(p);
	}

	/**
	 * Calls {@link Match#parent()}.
	 */
	@Override
	protected Match parent(Match n, Index<Match> i) {
		return n.parent();
	}

	/**
	 * Returns {@code {@link Match#length()} == 0}.
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return whether the length of the character sequence matched is zero
	 */
	@Attribute(description = "whether node is zero-width")
	public boolean zero(Match m, Collection<Match> c, Index<Match> i) {
		return m.zeroWidth();
	}

	/**
	 * Returns whether the match node was generated by an {@link Assertion}
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return whether the match node was generated by an {@link Assertion}
	 */
	@Attribute(description = "whether node is an assertion")
	public boolean assertion(Match m, Collection<Match> c, Index<Match> i) {
		return m.rule() instanceof Assertion;
	}

	/**
	 * Calls {@link Match#length()}.
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return the length of the character sequence matched
	 */
	@Attribute(description = "length of group matched")
	public int length(Match m, Collection<Match> c, Index<Match> i) {
		return m.length();
	}

	/**
	 * Returns the match was generated by an explit rule. This is a rule which
	 * has its own definition line and label in the grammar.
	 * 
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return whether the match is the result of an explicit rule
	 */
	@Attribute(description = "whether the rule generating this node is explicitly defined")
	public boolean explicit(Match m, Collection<Match> c, Index<Match> i) {
		return m.rule().label().t == Type.explicit;
	}

	/**
	 * Returns {@link Match#group()}.
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return the character sequence matched by the {@link Match}
	 */
	@Attribute(description = "group matched")
	public String group(Match m, Collection<Match> c, Index<Match> i) {
		return m.group();
	}

	/**
	 * Returns the label of the {@link Rule} generating the {@link Match}. For
	 * example, if the match was generated by
	 * 
	 * <pre>
	 * foo = [{quux} 'bar']
	 * </pre>
	 * 
	 * the value of this attribute will be "foo", though it also bears the label
	 * "quux".
	 * 
	 * @param m
	 *            context node
	 * @param c
	 *            context collection; required by method signature but ignored
	 * @param i
	 *            match tree index; required by method signature but ignored
	 * @return id of {@link Rule} generating the {@link Match}
	 */
	@Attribute(description = "rule generating match")
	public String label(Match m, Collection<Match> c, Index<Match> i) {
		return m.rule().label().id;
	}

	/**
	 * Prints out all attributes {@link MatchPath} can handle.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int length = 0;
		for (String s : MatchPath.standard().attributes.keySet())
			length = Math.max(length, s.length());
		String format = "@%-" + length + "s : %s%n%" + length + "s    %s%n";
		for (Entry<String, String[]> e : MatchPath.standard().attributes()
				.entrySet()) {
			System.out.printf(format, e.getKey(), e.getValue()[0], "",
					e.getValue()[1]);
		}
	}
}
