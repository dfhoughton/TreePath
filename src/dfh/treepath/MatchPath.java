package dfh.treepath;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dfh.grammar.Assertion;
import dfh.grammar.Match;

/**
 * A {@link Forester} adaptor for {@link Match} trees.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 */
public class MatchPath extends Forester<Match> {

	@Override
	protected List<Match> children(Match n, Index<Match> i) {
		List<Match> children = new ArrayList<Match>(n.children().length);
		for (Match m : n.children())
			children.add(m);
		return children;
	}

	@Override
	protected boolean hasTag(Match n, String tag) {
		return n.hasLabel(tag);
	}

	@Override
	protected boolean matchesTag(Match n, Pattern p) {
		return n.hasLabel(p);
	}

	@Override
	protected Match parent(Match n, Index<Match> i) {
		return n.parent();
	}

	@Attribute
	public boolean zeroWidth(Match m, Index<Match> i) {
		return m.zeroWidth();
	}

	@Attribute
	public boolean assertion(Match m, Index<Match> i) {
		return m.rule() instanceof Assertion;
	}
}
