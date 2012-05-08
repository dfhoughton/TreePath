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
 * <dt>u</dt>
 * <dd>utility function</dd>
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
	protected boolean matches(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return Pattern.matches(pattern, s);
	}

	@Attribute("s:starts-with")
	protected boolean startsWith(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.startsWith(pattern);
	}

	@Attribute("s:ends-with")
	protected boolean endsWith(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.endsWith(pattern);
	}

	@Attribute("s:contains")
	protected boolean contains(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.indexOf(pattern) > -1;
	}

	@Attribute("s:index")
	protected int index(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.indexOf(pattern);
	}

	/**
	 * @param n
	 * @param c
	 * @param i
	 * @param parts
	 * @return concatenation of the stringification of an arbitrary list of
	 *         objects
	 */
	@Attribute("s:concat")
	protected String concatenate(N n, Collection<N> c, Index<N> i,
			Object... parts) {
		StringBuilder b = new StringBuilder();
		for (Object o : parts)
			b.append(o);
		return b.toString();
	}

	@Attribute("m:max")
	protected Number max(N n, Collection<N> c, Index<N> i, Number... nums) {
		Number max = nums[0];
		for (int j = 1; j < nums.length; j++) {
			Number n2 = nums[j];
			if (max.doubleValue() < n2.doubleValue())
				max = n2;
		}
		return max;
	}

	@Attribute("m:min")
	protected Number min(N n, Collection<N> c, Index<N> i, Number... nums) {
		Number min = nums[0];
		for (int j = 1; j < nums.length; j++) {
			Number n2 = nums[j];
			if (min.doubleValue() > n2.doubleValue())
				min = n2;
		}
		return min;
	}

	@Attribute("m:sum")
	protected Double sum(N n, Collection<N> c, Index<N> i, Number... nums) {
		double sum = 0;
		for (Number num : nums)
			sum += num.doubleValue();
		return sum;
	}

	@Attribute("m:prod")
	protected Double product(N n, Collection<N> c, Index<N> i, Number... nums) {
		double product = 1;
		for (Number num : nums)
			product *= num.doubleValue();
		return product;
	}

	/**
	 * Returns {@link System#currentTimeMillis()}. Potentially useful in
	 * debugging.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @return {@link System#currentTimeMillis()}
	 */
	@Attribute("u:millis")
	protected Long millis(N n, Collection<N> c, Index<N> i) {
		return System.currentTimeMillis();
	}
}
