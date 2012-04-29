package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/&gt;~foo~</code> and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class ClosestMatching<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	public ClosestMatching(String pattern, Match arguments, Forester<N> f) {
		super(arguments, f);
		final Pattern p = Pattern.compile(pattern);
		test = new NodeTest<N>() {
			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.matchesTag(n, p);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		return i.f.closest(n, test, i);
	}

}
