package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;

/**
 * Instantiates the square bracket expressions such as [1], [foo], and
 * [@attribute].
 * <p>
 * 
 * @author David F. Houghton - Apr 29, 2012
 * 
 * @param <N>
 */
public abstract class Predicate<N> {

	public static <N> Predicate<N> build(Match m, Forester<N> f) {
		Match type = m.children()[2].children()[0];
		if (type.hasLabel("int"))
			return new IndexPredicate<N>(Integer.parseInt(type.group()));
		if (type.hasLabel("treepath"))
			return new TreePathPredicate<N>(type, f);
		return new ConditionalPredicate<N>(type, f);
	}

	public abstract Collection<N> filter(Collection<N> c, Index<N> i);
}
