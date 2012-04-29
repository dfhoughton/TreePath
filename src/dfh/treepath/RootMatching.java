package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/~foo~</code> and the like, where this is
 * the first expression in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class RootMatching<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	public RootMatching(String pattern, Match arguments, Forester<N> f) {
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
		return i.f.children(i.root, test, i);
	}

}
