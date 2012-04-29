package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;

public class ConditionalPredicate<N> extends Predicate<N> {
	private interface Expression<N> {
		abstract boolean test(N n, Index<N> i);
	}

	private static class PathExpression<N> implements Expression<N> {
		private final Path<N> path;

		PathExpression(Match m, Forester<N> f) {
			path = f.path(m);
		}

		@Override
		public boolean test(N n, Index<N> i) {
			return !path.select(n, i).isEmpty();
		}

	}

	private static class AttributeExpression<N> implements Expression<N> {
		private final CompiledAttribute<N> a;

		AttributeExpression(Match m, Forester<N> f) {
			a = new CompiledAttribute<N>(m, f);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean test(N n, Index<N> i) {
			Object o = a.apply(n, i);
			if (o == null)
				return false;
			if (o instanceof Boolean)
				return ((Boolean) o).booleanValue();
			if (o instanceof Collection<?>)
				return !((Collection<N>) o).isEmpty();
			if (o instanceof String)
				return ((String) o).length() > 0;
			if (o instanceof Number)
				return ((Number) o).doubleValue() != 0D;
			return true;
		}
	}

	private static class NotExpression<N> implements Expression<N> {
		Expression<N> e;

		NotExpression(Match m, Forester<N> f) {
			e = createExpression(m, f);
		}

		@Override
		public boolean test(N n, Index<N> i) {
			return !e.test(n, i);
		}
	}

	private static class AndExpression<N> implements Expression<N> {

		private final Expression<N>[] expressions;

		@SuppressWarnings("unchecked")
		AndExpression(List<Match> m, Forester<N> f) {
			expressions = new Expression[m.size()];
			int j = 0;
			for (Match n : m) {
				expressions[j++] = createExpression(n, f);
			}
		}

		@Override
		public boolean test(N n, Index<N> i) {
			for (Expression<N> e : expressions) {
				if (!e.test(n, i))
					return false;
			}
			return true;
		}

	}

	private static class OrExpression<N> implements Expression<N> {

		private final Expression<N>[] expressions;

		@SuppressWarnings("unchecked")
		OrExpression(List<Match> m, Forester<N> f) {
			expressions = new Expression[m.size()];
			int j = 0;
			for (Match n : m) {
				expressions[j++] = createExpression(n, f);
			}
		}

		@Override
		public boolean test(N n, Index<N> i) {
			for (Expression<N> e : expressions) {
				if (e.test(n, i))
					return true;
			}
			return false;
		}

	}

	private static class XorExpression<N> implements Expression<N> {

		private final Expression<N>[] expressions;

		@SuppressWarnings("unchecked")
		XorExpression(List<Match> m, Forester<N> f) {
			expressions = new Expression[m.size()];
			int j = 0;
			for (Match n : m) {
				expressions[j++] = createExpression(n, f);
			}
		}

		@Override
		public boolean test(N n, Index<N> i) {
			int count = 0;
			for (Expression<N> e : expressions) {
				if (!e.test(n, i))
					count++;
				if (count > 1)
					return false;
			}
			return count == 1;
		}

	}

	private final Expression<N> e;

	private static final MatchTest conditionMT = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("condition");
		}
	};

	public ConditionalPredicate(Match type, Forester<N> f) {
		e = createExpression(type, f);
	}

	private static <N> Expression<N> createExpression(Match type, Forester<N> f) {
		type = type.children()[1];
		String l = type.rule().label().id;
		Expression<N> ex = null;
		if (l.equals("term")) {
			if (type.children()[0].hasLabel("attribute"))
				ex = new AttributeExpression<N>(type.children()[0], f);
			else
				ex = new PathExpression<N>(type.children()[0], f);
		} else {
			List<Match> terms = type.closest(conditionMT);
			if (l.equals("not_cnd")) {
				ex = new NotExpression<N>(terms.get(0), f);
			} else if (l.equals("and_cnd")) {
				ex = new AndExpression<N>(terms, f);
			} else if (l.equals("or_cnd")) {
				ex = new OrExpression<N>(terms, f);
			} else if (l.equals("xor_cnd")) {
				ex = new XorExpression<N>(terms, f);
			} else {
				throw new PathException("unknown condition type: " + l);
			}
		}
		return ex;
	}

	@Override
	public Collection<N> filter(Collection<N> c, Index<N> i) {
		List<N> filtrate = new ArrayList<N>(c.size());
		for (N n : c) {
			if (e.test(n, i))
				filtrate.add(n);
		}
		return filtrate;
	}

}
