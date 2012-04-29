package dfh.treepath;

import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;

public abstract class TestSelector<N> implements Selector<N> {
	static final MatchTest argMT = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("predicate");
		}
	};
	protected final Predicate<N>[] predicates;

	@SuppressWarnings("unchecked")
	public TestSelector(Match arguments, Forester<N> f) {
		List<Match> argList = arguments.closest(argMT);
		predicates = new Predicate[argList.size()];
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = Predicate.build(argList.get(i), f);
		}
	}

	@Override
	public Collection<N> select(N n, Index<N> i) {
		Collection<N> candidates = candidates(n, i);
		for (Predicate<N> p : predicates) {
			candidates = p.filter(candidates, i);
			if (candidates.isEmpty())
				break;
		}
		return candidates;
	}

	protected abstract Collection<N> candidates(N n, Index<N> i);
}
