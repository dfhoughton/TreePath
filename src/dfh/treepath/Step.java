package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.treepath.PathGrammar.Axis;

public abstract class Step<N> implements Selector<N> {
	protected final Predicate<N>[] predicates;
	private static final Predicate<?>[] EMPTY = new Predicate[0];
	private static final MatchTest predicateMT = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("predicate");
		}
	};

	enum StepType {
		parent, self, root, global, children, axis
	}

	enum MatchType {
		literal, pattern, wildcard
	}

	private final Axis a;
	private final StepType t;
	protected final Forester<N> f;
	private final MatchType mt;

	private enum PathType {
		root, relative, global
	}

	public static <N> Step<N> makeStep(Forester<N> f, Match m, boolean first) {
		Match slash = m.children()[0], step = m.children()[1];
		if (step.hasLabel("abbreviated")) {
			switch (step.length()) {
			case 1:
				return new SelfSelector(f);
			case 2:
				default:
					throw new PathException("impossible abbreviated path: "
							+ step.group());
			}
		}
		PathType pt = null;
		if (first) {
			switch (slash.length()) {
			case 0:
				pt = PathType.relative;
				break;
			case 1:
				pt = PathType.root;
				break;
			case 2:
				pt = PathType.global;
			default:
				throw new PathException("impossible step separator: "
						+ slash.group());
			}
		} else {
			switch (slash.length()) {
			case 1:
				pt = PathType.relative;
				break;
			case 2:
				pt = PathType.global;
				break;
			default:
				throw new PathException(
						"impossible non-initial step separator: "
								+ slash.group());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Step(Forester<N> f, String sep, Match m, boolean first) {
		this.f = f;
		if (m.hasLabel("abbreviated")) {
			t = m.length() == 1 ? StepType.self : StepType.parent;
			predicates = (Predicate<N>[]) EMPTY;
			a = null;
			mt = null;
		} else {
			Match axisMatch = m.children()[0];
			String axisName = axisMatch.group();
			if (axisName.length() == 0) {
				if (sep == null)
					t = StepType.children;
				else if ("/".equals(sep)) {
					t = first ? StepType.root : StepType.children;
				} else
					t = StepType.global;
				a = null;
			} else {
				a = Axis.vo(axisName);
				t = StepType.axis;
			}
			Match fm = m.children()[1];
			if (fm.hasLabel("wildcard"))
				mt = MatchType.wildcard;
			else if (fm.hasLabel("specific")) {

			}
			List<Match> predList = m.closest(predicateMT);
			predicates = new Predicate[predList.size()];
			int i = 0;
			for (Match p : predList)
				predicates[i++] = makePredicate(p);
		}
	}

	private Predicate<N> makePredicate(Match p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<N> select(Collection<N> n) {
		if (n.isEmpty())
			return Collections.emptyList();
		Collection<N> selection;
		switch (t) {
		case self:
			selection = n;
			break;
		case parent:
			selection = new LinkedHashSet<N>((int) (n.size() * 1.75));
			for (N o : n)
				selection.add(f.parent(o));
			break;
		case root:
			selection = new ArrayList<N>(1);
			((List<N>) selection).add(f.root(n.iterator().next()));
			break;
		case global:
			selection = new LinkedHashSet<N>();
			for (N o : n) {
				selection.addAll(f.descendants(o));
				((Set<N>) selection).add(o);
			}
			break;
		case axis:
			Set<N> set = new LinkedHashSet<N>();
			selection = set;
			for (N o : n)
				set.addAll(f.axis(o, a));
			break;
		default:
			throw new PathException("cannot generate step type " + t);
		}
		// TODO handle label or pattern
		return null;
	}
}
