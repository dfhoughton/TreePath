package dfh.treepath;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * A generic {@link Forester} filled with useful {@link Attribute attributes}
 * (these are the functions). Basically, this {@link Forester} holds a bunch of
 * functions, mostly copied from XPath. If your {@link Forester} extends this
 * class, you get all these functions as well.
 * <p>
 * To reduce the incidence of namespace collisions, all the attributes in this
 * class have namespace prefixes and indicate their category of function:
 * <dl>
 * <dt>s</dt>
 * <dd>String function</dd>
 * <dt>m</dt>
 * <dd>mathematical function</dd>
 * </dl>
 * 
 * @author David F. Houghton - May 7, 2012
 * 
 * @param <N>
 */
public abstract class FunctionalForester<N> extends Forester<N> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public FunctionalForester(NodeTest<N>... nodeTest) {
	}

	@Attribute("s:matches")
	protected boolean matches(N n, Collection<N> c, Index<N> i, String pattern,
			String s) {
		return Pattern.matches(pattern, s);
	}

	@Attribute("s:starts-with")
	protected boolean startsWith(N n, Collection<N> c, Index<N> i,
			String pattern, String s) {
		return s.startsWith(pattern);
	}

	@Attribute("s:ends-with")
	protected boolean endsWith(N n, Collection<N> c, Index<N> i,
			String pattern, String s) {
		return s.endsWith(pattern);
	}

	@Attribute("s:contains")
	protected boolean contains(N n, Collection<N> c, Index<N> i,
			String pattern, String s) {
		return s.indexOf(pattern) > -1;
	}
}
