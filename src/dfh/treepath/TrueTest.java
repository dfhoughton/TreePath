package dfh.treepath;

public class TrueTest<N> implements NodeTest<N> {

	private TrueTest() {
	}

	@SuppressWarnings("rawtypes")
	private static final TrueTest<?> test = new TrueTest();

	@SuppressWarnings("unchecked")
	public static final <N> TrueTest<N> test() {
		return (TrueTest<N>) test;
	}

	@Override
	public boolean passes(N n, Index<N> i) {
		return true;
	}

}
