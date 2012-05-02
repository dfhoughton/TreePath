package dfh.treepath;

import java.io.Serializable;

public interface NodeTest<N> extends Serializable {
	boolean passes(N n, Index<N> i);
}
