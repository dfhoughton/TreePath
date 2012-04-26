package dfh.treepath;

import java.util.Collection;
import java.util.Collections;

public class SelfSelector<N> extends Step<N> {
	public SelfSelector(Forester<N> f) {
		this.f = f;
	}
	
	@Override
	public Collection<N> select(Collection<N> n) {
		return n;
	}
}
