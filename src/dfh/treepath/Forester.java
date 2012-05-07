package dfh.treepath;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * Foresters are path factories. Their chief method if interest is
 * {@link #path(String)}.
 * 
 * @author David F. Houghton - Apr 18, 2012
 * 
 * @param <N>
 *            the variety of node in the trees understood by the
 *            {@link Forester}
 */
public abstract class Forester<N> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected final static Map<Class<? extends Forester<?>>, Map<String, Method>> attributeCache = new HashMap<Class<? extends Forester<?>>, Map<String, Method>>();
	final Map<String, Method> attributes;
	final NodeTest<N>[] ignore;
	/**
	 * A place for the log attribute to send its logging.
	 */
	private PrintStream loggingStream = System.err;

	/**
	 * Initializes the map from attributes to methods.
	 */
	@SuppressWarnings("unchecked")
	public Forester(NodeTest<N>... ignorable) {
		if (ignorable.length > 0) {
			Set<NodeTest<N>> set = new HashSet<NodeTest<N>>();
			for (NodeTest<N> t : ignorable)
				set.add(t);
			ignore = set.toArray(new NodeTest[set.size()]);
		} else
			ignore = new NodeTest[0];
		attributes = getAttributes();
	}

	@SuppressWarnings("unchecked")
	protected final Map<String, Method> getAttributes() {
		synchronized (attributeCache) {
			Map<String, Method> map = attributeCache.get(this.getClass());
			if (map == null) {
				map = new HashMap<String, Method>();
				Class<?> icl = null;
				try {
					icl = Class.forName("java.util.Collection");
				} catch (ClassNotFoundException e) {
					throw new PathException(e);
				}
				Class<?> cz = getClass();
				while (Forester.class.isAssignableFrom(cz)) {
					for (Method m : cz.getDeclaredMethods()) {
						int mods = m.getModifiers();
						if (!Modifier.isPrivate(mods)) {
							Attribute a = m.getAnnotation(Attribute.class);
							if (a != null) {
								String name = a.value();
								if (name.length() == 0)
									name = m.getName();
								if (map.containsKey(name))
									continue;
								Class<?>[] pts = m.getParameterTypes();
								if (pts.length < 3)
									throw new PathException(
											"ill-formed attribute @"
													+ name
													+ "; every attribute must have at least a node, collection, and index parameter");
								if (!icl.isAssignableFrom(pts[1]))
									throw new PathException(
											"the second parameter for attribute @"
													+ name
													+ " must represent the collection of nodes of which the context node is a member");
								if (!Index.class.isAssignableFrom(pts[2]))
									throw new PathException(
											"the third parameter for attribute @"
													+ name
													+ " must be an instance of dfh.treepath.Index");
								if (m.getReturnType() == Void.TYPE)
									throw new PathException("attribute @"
											+ name
											+ " does not return any value");
								m.setAccessible(true);
								map.put(name, m);
							}
						}
					}
					cz = cz.getSuperclass();
				}
				attributeCache.put(
						(Class<? extends Forester<?>>) this.getClass(), map);
			}
			return map;
		}
	}

	protected static final MatchTest pathMt = new MatchTest() {
		private static final long serialVersionUID = 1L;

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
		case sibling:
			return siblings(n, t, i);
		case siblingOrSelf:
			return siblingsOrSelf(n, t, i);
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

	protected Collection<N> siblings(N n, NodeTest<N> t, Index<N> i) {
		List<N> siblings = siblings(parent(n, i), i);
		if (siblings.isEmpty())
			return siblings;
		List<N> list = new ArrayList<N>(siblings.size());
		for (N s : siblings)
			if (t.passes(s, i))
				list.add(s);
		return list;
	}

	protected Collection<N> siblingsOrSelf(N n, NodeTest<N> t, Index<N> i) {
		if (i.isRoot(n)) {
			List<N> list = new ArrayList<N>(1);
			if (t.passes(n, i))
				list.add(n);
			return list;
		}
		List<N> siblings = kids(parent(n, i), i);
		List<N> list = new ArrayList<N>(siblings.size());
		for (N s : siblings)
			if (t.passes(s, i))
				list.add(s);
		return list;
	}

	protected List<N> children(N n, NodeTest<N> t, Index<N> i) {
		List<N> children = kids(n, i);
		if (children.isEmpty())
			return children;
		List<N> list = new ArrayList<N>(children.size());
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

	private static final MatchTest segmentMT = new MatchTest() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean test(Match m) {
			return m.hasLabel("segment");
		}
	};
	private static final MatchTest anameMT = new MatchTest() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("axis_name");
		}
	};

	private Selector<N>[] makePath(Match match) {
		List<Match> subsequentSteps = match.closest(segmentMT);
		@SuppressWarnings("unchecked")
		Selector<N>[] path = new Selector[subsequentSteps.size()];
		int i = 0;
		for (Match m : subsequentSteps) {
			path[i] = makeStep(m, i++ == 0);
		}
		return path;
	}

	private Selector<N> makeStep(Match fs, boolean first) {
		Match slash, step;
		slash = fs.children()[0];
		step = fs.children()[1];
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
		Match predicates = step.children()[1];
		Match tagMatch = step.children()[0].children()[0].children()[1];
		String s = tagMatch.group();
		if ("*".equals(s))
			return new ClosestWildcard<N>(predicates, this);
		else if (s.charAt(0) == '~') {
			s = cleanMatch(s);
			return new ClosestMatching<N>(s, predicates, this);
		} else
			return new ClosestTag<N>(s, predicates, this);
	}

	/**
	 * Makes a //foo step
	 * 
	 * @param step
	 * @return
	 */
	private Selector<N> makeGlobalStep(Match step) {
		Match tagMatch = step.children()[0], predicates = step.children()[1];
		String s = tagMatch.group();
		if ("*".equals(s))
			return new AnywhereWildcard<N>(predicates, this);
		else if (s.charAt(0) == '~') {
			s = cleanMatch(s);
			return new AnywhereMatching<N>(s, predicates, this);
		} else
			return new AnywhereTag<N>(s, predicates, this);
	}

	private Selector<N> makeRootStep(Match step) {
		Match predicates = step.children()[1];
		step = step.children()[0].children()[0];
		if (step.rule().label().id.equals("abbreviated")) {
			step = step.children()[1];
			switch (step.length()) {
			case 1:
				return new RootSelector<N>(predicates, this);
			case 2:
				new PathException(
						"/.. is ill-formed; the root node has no parent");
			default:
				return new IdSelector<N>(step, predicates);
			}
		} else {
			Match axisMatch = step.children()[0], tagMatch = step.children()[1];
			String s = tagMatch.group();
			if (axisMatch.length() == 0) {
				if ("*".equals(s))
					return new RootWildcard<N>(predicates, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new RootMatching<N>(s, predicates, this);
				} else
					return new RootTag<N>(s, predicates, this);
			} else {
				String aname = axisMatch.first(anameMT).group();
				if ("*".equals(s))
					return new RootAxisWildcard<N>(aname, predicates, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new RootAxisMatching<N>(aname, s, predicates, this);
				} else
					return new RootAxisTag<N>(aname, s, predicates, this);
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
		Match predicates = step.children()[1];
		step = step.children()[0].children()[0];
		if (step.rule().label().id.equals("abbreviated")) {
			step = step.children()[1];
			switch (step.length()) {
			case 1:
				return new SelfSelector<N>(predicates, this);
			case 2:
				new ParentSelector<N>(predicates, this);
			default:
				return new IdSelector<N>(step, predicates);
			}
		} else {
			Match axisMatch = step.children()[0], tagMatch = step.children()[1];
			String s = tagMatch.group();
			if (axisMatch.length() == 0) {
				if ("*".equals(s))
					return new ChildWildcard<N>(predicates, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new ChildMatching<N>(s, predicates, this);
				} else
					return new ChildTag<N>(s, predicates, this);
			} else {
				String aname = axisMatch.first(anameMT).group();
				if ("*".equals(s))
					return new AxisWildcard<N>(aname, predicates, this);
				else if (s.charAt(0) == '~') {
					s = cleanMatch(s);
					return new AxisMatching<N>(aname, s, predicates, this);
				} else
					return new AxisTag<N>(aname, s, predicates, this);
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
		List<N> children = kids(n, i);
		if (children.isEmpty())
			return true;
		return false;
	}

	/**
	 * An attribute for selecting a member from a collection of nodes returned
	 * by a path. E.g., @pick(foo//bar, 1). The index is zero-based.
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @param from
	 *            a collection of candidates selected by some path
	 * @param index
	 *            the index in the collection selected from corresponding to the
	 *            node selected
	 * @return the node selected, or <code>null</code> if there is no
	 *         appropriate node
	 */
	@Attribute
	protected N pick(N n, Collection<N> c, Index<N> i, Collection<N> from,
			int index) {
		int j = index;
		if (from.isEmpty())
			return null;
		if (index < 0)
			j = from.size() + index;
		if (j < 0)
			return null;
		if (j >= from.size())
			return null;
		if (from instanceof List<?>)
			return ((List<N>) from).get(j);
		int in = 0;
		for (Iterator<N> it = from.iterator(); it.hasNext();) {
			N next = it.next();
			if (in++ == j)
				return next;
		}
		return null;
	}

	/**
	 * An attribute for selecting a member from a collection of nodes returned
	 * by a path. E.g., @size(foo//bar).
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @param path
	 *            a path used to select a candidate set; the path is evaluated
	 *            relative to the context node
	 * @return the number of nodes selected by the path
	 */
	@Attribute
	protected int size(N n, Collection<N> c, Index<N> i, Collection<N> from) {
		return from.size();
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
	 * @param n
	 * @param c
	 * @param i
	 * @return the identifying string, if any, of this node
	 */
	@Attribute
	protected String id(N n, Collection<N> c, Index<N> i) {
		return i.id(n);
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
		List<N> children = kids(n, i);
		if (children.isEmpty()) {
			if (!t.passes(n, i))
				return Collections.emptyList();
			List<N> leaves = new ArrayList<N>(1);
			leaves.add(n);
			return leaves;
		}
		List<N> leaves = new ArrayList<N>();
		for (N child : children)
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
		List<N> children = kids(n, i);
		if (children.isEmpty())
			return children;
		List<N> descendants = new LinkedList<N>();
		for (N child : children) {
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
		List<N> children = kids(n, i);
		if (children.isEmpty())
			return children;
		List<N> closest = new LinkedList<N>();
		for (N child : children)
			closest.addAll(closest(child, t, i));
		return closest;
	}

	protected List<N> precedingSiblings(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		List<N> siblings = kids(parent(n, i), i);
		if (siblings.size() == 1)
			return Collections.emptyList();
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
		List<N> siblings = kids(parent(n, i), i);
		if (siblings.size() == 1)
			return Collections.emptyList();
		List<N> sibs = new ArrayList<N>(siblings.size() - 1);
		for (N s : siblings) {
			if (s != n)
				sibs.add(s);
		}
		return sibs;
	}

	protected List<N> siblingsOrSelf(N n, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		return kids(parent(n, i), i);
	}

	protected List<N> followingSiblings(N n, NodeTest<N> t, Index<N> i) {
		if (isRoot(n, null, i))
			return Collections.emptyList();
		List<N> siblings = kids(parent(n, i), i);
		if (siblings.size() == 1)
			return Collections.emptyList();
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
		List<N> siblings = kids(parent(n, in), in);
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
	 * Like {@link #children(Object, Index)}, but it tests the nodes against the
	 * node tests in {@link #ignore}.
	 * 
	 * @param n
	 * @param i
	 * @return
	 */
	protected final List<N> kids(N n, Index<N> i) {
		List<N> children = children(n, i);
		if (children == null)
			return Collections.emptyList();
		if (ignore.length == 0 || children.isEmpty())
			return children;
		List<N> kids = new ArrayList<N>(children.size());
		OUTER: for (N c : children) {
			for (NodeTest<N> t : ignore)
				if (t.passes(c, i))
					continue OUTER;
			kids.add(c);
		}
		return kids;
	}

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
	 * foresters that require more of an index than a map from identifiers to
	 * nodes and references back to the forester and the tree root.
	 * 
	 * @param root
	 * @return and index of the given tree
	 */
	public Index<N> index(N root) {
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

	/**
	 * @return the {@link PrintStream} used by the
	 *         {@link #log(Object, Collection, Index, Object)} attribute
	 */
	public PrintStream getLoggingStream() {
		return loggingStream;
	}

	/**
	 * @param loggingStream
	 *            the {@link PrintStream} used by the
	 *            {@link #log(Object, Collection, Index, Object)} attribute
	 */
	public void setLoggingStream(PrintStream loggingStream) {
		this.loggingStream = loggingStream;
	}

	/**
	 * A debugging attribute. It performs no filtering but can be used to output
	 * messages to a debugging stream.
	 * 
	 * @param n
	 *            context node; ignored by this attribute but required in the
	 *            method signature
	 * @param c
	 *            context node collection; ignored by this attribute but
	 *            required in the method signature
	 * @param i
	 *            tree index; ignored by this attribute but required in the
	 *            method signature
	 * @param msg
	 *            object printed out as a message
	 * @return
	 */
	@Attribute
	protected final Boolean log(N n, Collection<N> c, Index<N> i, Object msg) {
		loggingStream.println(msg.toString());
		return Boolean.TRUE;
	}

	/**
	 * A convenience method that returns the value of the given attribute for a
	 * particular node in a generic context. This method delegates to
	 * {@link #attribute(Object, String, Collection, Index, Object...)}, setting
	 * the context collection and tree index to null.
	 * 
	 * @param node
	 *            context node
	 * @param name
	 *            attribute name
	 * @param parameters
	 *            any attribute parameters
	 * @return the value of the given attribute for the given node evaluated in
	 *         a generic context
	 */
	public Object attribute(N node, String name, Object... parameters) {
		return attribute(node, name, null, null, parameters);
	}

	/**
	 * A convenience method that returns the value of the given attribute for a
	 * particular node in a particular context. If the collection or index is
	 * null, a fresh one wil be created. The collection will be a single member
	 * list containing only the context node. The index will be the index
	 * generated by {@link #index(Object)}, which treats the context node as the
	 * root of its own tree.
	 * <p>
	 * If you can calculate the attribute value directly, this will be more
	 * efficient as it will involve no reflection and possible guesswork as to
	 * the evaluation context.
	 * 
	 * @param node
	 *            context node
	 * @param name
	 *            attribute name
	 * @param c
	 *            a selection context -- the set of nodes the context node has
	 *            been selected with
	 * @param i
	 *            tree index
	 * @param parameters
	 *            any parameters for the attribute
	 * @return the value of the given attribute for the given node selected in
	 *         the given context
	 */
	public Object attribute(N node, String name, Collection<N> c, Index<N> i,
			Object... parameters) {
		if (node == null)
			throw new PathException(
					"attributes cannot be evaluated on null nodes");
		Method m = attributes.get(name);
		if (m == null)
			throw new PathException("unknown attribute: " + name);
		if (i == null) {
			i = index(node);
		}
		if (c == null) {
			c = new ArrayList<N>(1);
			c.add(node);
		}
		if (!i.indexed())
			i.index();
		try {
			List<Object> parameterList = new ArrayList<Object>(
					parameters.length + 3);
			parameterList.add(node);
			parameterList.add(c);
			parameterList.add(i);
			for (Object o : parameters)
				parameterList.add(o);
			return m.invoke(this, parameterList.toArray());
		} catch (Exception e) {
			throw new PathException("could not evaluate attribute " + name
					+ " with node, index, collection, and parameters provided",
					e);
		}
	}

	/**
	 * Returns a string uniquely identifying the node in the tree as a sequence
	 * of branch indices. For instance, the root node will be "/", the leftmost
	 * node under the root will be "/0", the second node under the leftmost node
	 * under root will be "/0/1", and so on.
	 * <p>
	 * This attribute is chiefly useful in debugging.
	 * 
	 * @param n
	 *            context node
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @return <code>true</code>
	 */
	@Attribute
	protected final String uid(N n, Collection<N> c, Index<N> i) {
		List<Integer> list = new ArrayList<Integer>();
		N node = n;
		while (node != i.root) {
			N parent = i.f.parent(node, i);
			List<N> children = i.f.children(parent, i);
			for (int in = 0, lim = children.size(); in < lim; in++) {
				N child = children.get(in);
				if (child == node) {
					list.add(in);
					break;
				}
			}
			node = parent;
		}
		if (list.isEmpty())
			return "/";
		StringBuilder b = new StringBuilder();
		for (int in = list.size() - 1; in >= 0; in--)
			b.append('/').append(list.get(in));
		return b.toString();
	}
}
