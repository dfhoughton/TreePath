package dfh.treepath;

/**
 * <p>
 * 
 * @author David F. Houghton - Apr 18, 2012
 * 
 * @param <N>
 *            node type
 */
public abstract class Forester<N> {

	public Path<N> path(String path) {
		return null;
	}

	/**
	 * @param n
	 * @param i
	 * @return the ith child of n
	 */
	public abstract N child(N n, int i);
}
