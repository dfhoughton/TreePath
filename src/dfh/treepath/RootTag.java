package dfh.treepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dfh.grammar.Match;

/**
 * {@link Selector} implementing <code>/foo</code> and the like, where this is
 * the first expression in the path.
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public class RootTag<N> extends TestSelector<N> {

	private final NodeTest<N> test;

	public RootTag(final String tag, Match arguments, Forester<N> f) {
		super(arguments, f);
		test = new NodeTest<N>() {
			@Override
			public boolean passes(N n, Index<N> i) {
				return i.f.hasTag(n, tag);
			}
		};
	}

	@Override
	protected Collection<N> candidates(N n, Index<N> i) {
		if (test.passes(i.root, i)) {
			List<N> list = new ArrayList<N>(1);
			list.add(i.root);
			return list;
		}
		return Collections.emptyList();
	}

}
