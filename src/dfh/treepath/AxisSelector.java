package dfh.treepath;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

abstract class AxisSelector<N> extends TestSelector<N> {
	private static final long serialVersionUID = 1L;

	protected final Axis axis;

	AxisSelector(String axisName, Match arguments, Forester<N> f) {
		super(arguments, f);
		axis = Axis.vo(axisName);
	}
}
