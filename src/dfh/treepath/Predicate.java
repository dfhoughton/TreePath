package dfh.treepath;

import java.io.Serializable;
import java.util.Collection;

import dfh.grammar.Match;

/**
 * Instantiates the square bracket expressions such as [1], [foo], and
 * [&#064;attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
abstract class Predicate<N> implements Serializable {

	private static final long serialVersionUID = 1L;

	static <N> Predicate<N> build(Match m, Forester<N> f) {
		Match type = m.children()[2].children()[0];
		if (type.rule().label().id.equals("signed_int"))
			return new IndexPredicate<N>(Integer.parseInt(type.group()));
		if (type.rule().label().id.equals("treepath"))
			return new TreePathPredicate<N>(type, f);
		if (type.rule().label().id.equals("attribute_test"))
			return new AttributeTestPredicate<N>(type, f);
		return new ConditionalPredicate<N>(type, f);
	}

	abstract Collection<N> filter(Collection<N> c, Index<N> i);
}
