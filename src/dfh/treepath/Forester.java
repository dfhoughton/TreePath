package dfh.treepath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import dfh.grammar.GrammarException;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.grammar.Matcher;
import dfh.grammar.Options;
import dfh.treepath.PathGrammar.Axis;

/**
 * An expert on trees. A {@link Forester} answers questions about the variety of
 * trees it knows provided these quests are asked in the form of tree path
 * expressions.
 * <p>
 * 
 * @author David F. Houghton - Apr 18, 2012
 * 
 * @param <N>
 *            the variety of node in the trees understood by the
 *            {@link Forester}
 */
public abstract class Forester<N> {
	Map<String, Method> attributes = new HashMap<String, Method>();

	/**
	 * Initializes the map from attributes to methods.
	 */
	public Forester() {
		for (Method m : this.getClass().getMethods()) {
			Attribute a = m.getAnnotation(Attribute.class);
			if (a != null) {
				String name = a.value();
				if (name.length() == 0)
					name = m.getName();
				attributes.put(name, m);
			}
		}
	}

	protected static final MatchTest pathMt = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("path");
		}
	};

	/**
	 * Translates the return value of an {@link Attribute} into a boolean value.
	 * {@link Boolean} objects are interpreted via
	 * {@link Boolean#booleanValue()}. {@link Collection} objects are true if
	 * {@link Collection#isEmpty()} is false. All other values are true if they
	 * are not null.
	 * 
	 * @param o
	 * @return whether the value returned by an attribute evaluates to
	 *         <code>true</code>
	 */
	protected boolean attribToBoolean(Object o) {
		if (o instanceof Boolean)
			return ((Boolean) o).booleanValue();
		if (o instanceof Collection<?>)
			return !((Collection<?>) o).isEmpty();
		return o != null;
	}

	/**
	 * Translates a node and an axis into the appropriate collection of nodes.
	 * 
	 * @param n
	 * @param name
	 * @param i
	 * @return the nodes pertaining to a particular axis relative to the context
	 *         node
	 */
	protected Collection<N> axis(N n, Axis a, NodeTest<N> t, Index<N> i) {
		switch (a) {
		case child:
			return children(n, t, i);
		case ancestor:
			return ancestors(n, t, i);
		case ancestorOrSelf:
			List<N> list = ancestors(n, t, i);
			if (t.passes(n, i))
				list.add(n);
			return list;
		case descendant:
			return descendants(n, t, i);
		case descendantOrSelf:
			list = new ArrayList<N>(descendants(n, t, i));
			if (t.passes(n, i))
				list.add(n);
			return list;
		case following:
			return following(n, t, i);
		case followingSibling:
			return followingSiblings(n, t, i);
		case preceding:
			return preceding(n, t, i);
		case precedingSibling:
			return precedingSiblings(n, t, i);
		case leaf:
			return leaves(n, t, i);
		case self:
			if (t.passes(n, i)) {
				list = new ArrayList<N>(1);
				list.add(n);
				return list;
			}
			return Collections.emptyList();
		case parent:
			N parent = parent(n, i);
			if (t.passes(parent, i)) {
				list = new ArrayList<N>(1);
				list.add(parent);
				return list;
			}
			return Collections.emptyList();
		default:
			throw new PathException(Forester.class
					+ " not written to handle axis " + a);
		}
	}

	protected List<N> children(N n, NodeTest<N> t, Index<N> i) {
		List<N> children = children(n, i), list = new ArrayList<N>(
				children.size());
		for (N c : children) {
			if (t.passes(c, i))
				list.add(c);
		}
		return list;
	}

	/**
	 * @param path
	 * @return compiled path expression
	 */
	public Path<N> path(String path) {
		if (path == null)
			throw new PathException("path expression cannot be null");
		try {
			Matcher m = PathGrammar.g.matches(path, new Options()
					.keepRightmost(true).study(false));
			Match n = m.match();
			if (n == null) {
				StringBuilder b = new StringBuilder("could not parse ");
				b.append(path)
						.append(" as a tree path; check syntax at offset marked by '<HERE>': ");
				n = m.rightmostMatch();
				if (n == null)
					b.append("<HERE>").append(path);
				else if (n.end() == path.length())
					b.append(path).append("<HERE>");
				else {
					b.append(path.substring(0, n.end()));
					b.append("<HERE>");
					b.append(path.substring(n.end()));
				}
				throw new PathException(b.toString());
			}
			return path(n);
		} catch (GrammarException e) {
			throw new PathException("failed to compile path " + path, e);
		}
	}

	Path<N> path(Match n) {
		List<Match> paths = n.closest(pathMt);
		@SuppressWarnings("unchecked")
		Selector<N>[][] selectors = new Selector[paths.size()][];
		for (int i = 0; i < selectors.length; i++) {
			selectors[i] = makePath(paths.get(i));
		}
		return new Path<N>(this, selectors);
	}

	private static final MatchTest subsequentMT = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.hasLabel("segment");
		}
	};
	private static final MatchTest anameMT = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("axis_name");
		}
	};

	private Selector<N>[] makePath(Match match) {
		List<Match> subsequentSteps = match.closest(subsequentMT);
		@SuppressWarnings("unchecked")
		Selector<N>[] path = new Selector[subsequentSteps.size()];
		int i = 0;
		for (Match m : subsequentSteps) {
			path[i] = makeStep(m, i++ == 0);
		}
		return path;
	}

	private Selector<N> makeStep(Match fs, boolean first) {
		Match slash = fs.children()[0], step = fs.children()[1];
		switch (slash.length()) {
		case 0:
			return makeRelativeStep(step);
		case 1:
			return first ? makeRootStep(step) : makeRelativeStep(step);
		case 2:
			return slash.group().charAt(1) == '/' ? makeGlobalStep(step)
					: makeClosestStep(step);
		default:
			throw new PathException("unexpected step separator in path: "
					+ slash.group());
		}
	}

	private Selector<N> makeClosestStep(Match step) {
		step = step.children()[0];
		Match tagMatch = step.children()[1], arguments = step.children()[2];
		String s = tagMatch.group();
		if ("*".equals(s))
			return new ClosestWildcard<N>(arguments, this);
		else if (s.charAt(0) == '~') {
			s = cleanMatch(s);
			return new ClosestMatching<N>(s, arguments, this);
		} else
			return new ClosestTag<N>(s, arguments, this);
	}

	/**
	 * Makes a //foo step
	 * 
	 * @param step
	 * @return
	 */
	private Selector<N> makeGlobalStep(Match step) {
		step = step.children()[0];
		Match tagMatch = step.children()[1], arguments = step.children()[2];
		String s = tagMatch.group();
		if ("*".equals(s))
			return new AnywhereWildcard<N>(arguments, this);
		else if (s.charAt(0) == '~') {
			s = cleanMatch(s);
			return new AnywhereMatching<N>(s, arguments, this);
		} else
			return new AnywhereTag<N>(s, arguments, this);
	}

	private Selector<N> makeRootStep(Match step) {
		step = step.children()[0];
		if (step.hasLabel("abbreviated")) {
			if (step.length() == 1)
				return new RootSelector<N>();
			throw new PathException(
					"/.. is ill-formed; the root node has no parent");
		} else {
			Match axisMatch = step.children()[0], tagMatch = step.children()[1], arguments = step
					.children()[2];
			String s = tagMatch.group();
			if (axisMatch.length() == 0) {
				if ("*".equals(s))
					return new RootWildcard<N>(arguments, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new RootMatching<N>(s, arguments, this);
				} else
					return new RootTag<N>(s, arguments, this);
			} else {
				String aname = axisMatch.first(anameMT).group();
				if ("*".equals(s))
					return new RootAxisWildcard<N>(aname, arguments, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new RootAxisMatching<N>(aname, s, arguments, this);
				} else
					return new RootAxisTag<N>(aname, s, arguments, this);
			}
		}
	}

	/**
	 * Converts a ~&lt;chars&gt;~ expression into a string that can be compiled
	 * into a pattern.
	 * 
	 * @param s
	 * @return
	 */
	private String cleanMatch(String s) {
		s = s.substring(1, s.length() - 1).replaceAll("\\\\~", "~");
		return s;
	}

	private Selector<N> makeRelativeStep(Match step) {
		step = step.children()[0];
		if (step.hasLabel("abbreviated")) {
			if (step.length() == 1)
				return new RootSelector<N>();
			return new ParentSelector<N>();
		} else {
			Match axisMatch = step.children()[0], tagMatch = step.children()[1], arguments = step
					.children()[2];
			String s = tagMatch.group();
			if (axisMatch.length() == 0) {
				if ("*".equals(s))
					return new ChildWildcard<N>(arguments, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new ChildMatching<N>(s, arguments, this);
				} else
					return new ChildTag<N>(s, arguments, this);
			} else {
				String aname = axisMatch.first(anameMT).group();
				if ("*".equals(s))
					return new AxisWildcard<N>(aname, arguments, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new AxisMatching<N>(aname, s, arguments, this);
				} else
					return new AxisTag<N>(aname, s, arguments, this);
			}
		}
	}

	/**
	 * @param n
	 * @param i
	 * @return the ith child of n
	 */
	protected N child(N n, int i, Index<N> in) {
		List<N> children = children(n, in);
		if (children == null || children.isEmpty())
			return null;
		return children.get(i);
	}

	/**
	 * A boolean attribute that evaluates to true if the context node is a leaf.
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @return whether n is a leaf
	 */
	@Attribute("leaf")
	protected boolean isLeaf(N n, Collection<N> c, Index<N> i) {
		List<N> children = children(n, i);
		if (children == null || children.isEmpty())
			return true;
		return false;
	}

	/**
	 * A boolean attribute that evalues to true if the context node is the tree
	 * root.
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @return whether n is the tree root
	 */
	@Attribute("root")
	protected boolean isRoot(N n, Collection<N> c, Index<N> i) {
		return i.isRoot(n);
	}

	/**
	 * An attribute whose value is always <code>null</code>. This attribute
	 * cannot be overridden.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @return <code>null</code>
	 */
	@Attribute("null")
	protected final Object Null(N n, Collection<N> c, Index<N> i) {
		return null;
	}

	/**
	 * An attribute whose value is always <code>true</code>. This attribute
	 * cannot be overridden.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @return <code>true</code>
	 */
	@Attribute("true")
	protected final Boolean True(N n, Collection<N> c, Index<N> i) {
		return Boolean.TRUE;
	}

	/**
	 * An attribute whose value is always <code>false</code>. This attribute
	 * cannot be overridden.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @return <code>false</code>
	 */
	@Attribute("false")
	protected final Boolean False(N n, Collection<N> c, Index<N> i) {
		return Boolean.FALSE;
	}

	/**
	 * A boolean attribute that is true if the object parameter is not null.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @param o
	 *            value to test
	 * @return whether o isn't null
	 */
	@Attribute
	protected final Boolean defined(N n, Collection<N> c, Index<N> i, Object o) {
		return o == null ? false : true;
	}

	/**
	 * An attribute that returns the context node itself.
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @return the context node n
	 */
	@Attribute("this")
	protected final N This(N n, Collection<N> c, Index<N> i) {
		return n;
	}

	protected List<N> leaves(N n, NodeTest<N> t, Index<N> i) {
		if (isLeaf(n, null, i)) {
			if (!t.passes(n, i))
				return Collections.emptyList();
			List<N> leaves = new ArrayList<N>(1);
			leaves.add(n);
			return leaves;
		}
		List<N> leaves = new ArrayList<N>();
		for (N child : children(n, i))
			leaves.addAll(leaves(child, t, i));
		return leaves;
	}

	protected List<N> ancestors(N n, NodeTest<N> t, Index<N> i) {
		LinkedList<N> ancestors = new LinkedList<N>();
		N o = n;
		while (!isRoot(o, null, i)) {
			N parent = parent(o, i);
			if (t.passes(parent, i))
				ancestors.addFirst(parent);
			o = parent;
		}
		return ancestors;
	}

	protected Collection<N> descendants(N n, NodeTest<N> t, Index<N> i) {
		List<N> descendants = new LinkedList<N>();
		for (N child : children(n, i)) {
			if (!isLeaf(child, null, i))
				descendants.addAll(descendants(child, t, i));
			if (t.passes(child, i))
				descendants.add(child);
		}
		return descendants;
	}

	protected Collection<N> closest(N n, NodeTest<N> t, Index<N> i) {
		if (t.passes(n, i)) {
			List<N> list = new ArrayList<N>(1);
			list.add(n);
			return list;
		}
		List<N> closest = new LinkedList<N>();
		for (N child : children(n, i))
			closest.addAll(closest(child, t, i));
		return closest;
	}

	protected List<N> precedingSiblings(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		List<N> siblings = children(parent(n, i), i);
		List<N> precedingSiblings = new ArrayList<N>(siblings.size() - 1);
		for (N sib : siblings) {
			if (sib == n)
				break;
			if (t.passes(sib, i))
				precedingSiblings.add(sib);
		}
		return precedingSiblings;
	}

	protected List<N> siblings(N n, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		List<N> siblings = children(parent(n, i), i);
		List<N> sibs = new ArrayList<N>(siblings.size() - 1);
		for (N s : siblings) {
			if (s != n)
				sibs.add(s);
		}
		return sibs;
	}

	protected List<N> followingSiblings(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		List<N> siblings = children(parent(n, i), i);
		List<N> followingSiblings = new ArrayList<N>(siblings.size() - 1);
		boolean add = false;
		for (N sib : siblings) {
			if (add && t.passes(sib, i))
				followingSiblings.add(sib);
			else
				add = sib == n;
		}
		return followingSiblings;
	}

	@SuppressWarnings("unchecked")
	protected Collection<N> preceding(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		Collection<N> preceding = new LinkedList<N>();
		Collections.emptyList();
		List<N> ancestors = ancestors(n, (NodeTest<N>) TrueTest.test(), i);
		for (N a : ancestors.subList(1, ancestors.size())) {
			for (N p : precedingSiblings(a, (NodeTest<N>) TrueTest.test(), i)) {
				preceding.addAll(descendants(p, t, i));
				if (t.passes(p, i))
					preceding.add(p);
			}
		}
		for (N p : precedingSiblings(n, (NodeTest<N>) TrueTest.test(), i)) {
			preceding.addAll(descendants(p, t, i));
			if (t.passes(p, i))
				preceding.add(p);
		}
		return preceding;
	}

	@SuppressWarnings("unchecked")
	protected Collection<N> following(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		Collection<N> following = new LinkedList<N>();
		List<N> ancestors = ancestors(n, (NodeTest<N>) TrueTest.test(), i);
		for (N a : ancestors.subList(1, ancestors.size())) {
			for (N p : followingSiblings(a, (NodeTest<N>) TrueTest.test(), i)) {
				following.addAll(descendants(p, t, i));
				if (t.passes(p, i))
					following.add(p);
			}
		}
		for (N p : followingSiblings(n, (NodeTest<N>) TrueTest.test(), i)) {
			following.addAll(descendants(p, t, i));
			if (t.passes(p, i))
				following.add(p);
		}
		return following;
	}

	/**
	 * @param n
	 * @return index of n among its parent's children; -1 if n is root
	 */
	@Attribute
	protected int index(N n, Collection<N> c, Index<N> in) {
		if (isRoot(n, null, in))
			return -1;
		List<N> siblings = children(parent(n, in), in);
		for (int i = 0, lim = siblings.size(); i < lim; i++) {
			N o = siblings.get(i);
			if (o == n)
				return i;
		}
		return -1;
	}

	/**
	 * @param n
	 * @param i
	 * @param t
	 * @return the children of n
	 */
	protected abstract List<N> children(N n, Index<N> i);

	/**
	 * @param n
	 * @param tag
	 * @return whether the node bears the given tag
	 */
	protected abstract boolean hasTag(N n, String tag);

	/**
	 * @param n
	 * @param p
	 * @return whether the pattern matches the node's tag or tags
	 */
	protected abstract boolean matchesTag(N n, Pattern p);

	/**
	 * @param n
	 * @param i
	 * @param t
	 * @return the parent of n
	 */
	protected abstract N parent(N n, Index<N> i);

	/**
	 * @param n
	 * @return the root of the tree containing n
	 */
	protected N root(N n, Index<N> i) {
		return i.root;
	}

	/**
	 * Creates an {@link Index} of the given tree. It must be overridden by
	 * foresters that require more of an index than a reference back to the
	 * forester and the tree root.
	 * 
	 * @param root
	 * @return and index of the given tree
	 */
	protected Index<N> treeIndex(N root) {
		return new Index<N>(root, this);
	}

	/**
	 * Returns an alphabetized collection of the attributes this
	 * {@link Forester} can handle.
	 * 
	 * @return an alphabetized collection of the attributes this
	 *         {@link Forester} can handle
	 */
	public Set<String> attributes() {
		return new TreeSet<String>(attributes.keySet());
	}
}
