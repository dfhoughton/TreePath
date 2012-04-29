package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

/**
 * {@link Selector} implementing <code>//~foo~</code> and the like.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class AnywhereMatching<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	public AnywhereMatching(String pattern, Match arguments, Forester<N> f) {
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
		return i.f.axis(n, Axis.descendantOrSelf, test, i);
	}

}
