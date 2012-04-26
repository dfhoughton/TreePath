package dfh.treepath;

public interface Predicate<N> {
	public boolean test(Forester<N> f, N n);
}
