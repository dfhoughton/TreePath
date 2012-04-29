package dfh.treepath;

import dfh.grammar.Match;
import dfh.treepath.PathGrammar.Axis;

public abstract class AxisSelector<N> extends TestSelector<N> {

	protected final Axis axis;

	public AxisSelector(String axisName, Match arguments, Forester<N> f) {
		super(arguments, f);
		axis = Axis.vo(axisName);
	}
}
