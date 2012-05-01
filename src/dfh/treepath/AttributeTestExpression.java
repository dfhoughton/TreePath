package dfh.treepath;

import java.util.Collection;

import dfh.grammar.Match;
import dfh.treepath.ConditionalPredicate.Expression;

/**
 * An expression that evaluates the various attribute test expressions:
 * <code>@foo = 'bar'</code>, <code>@bar &lt @quux</code>, etc.
 * <p>
 * 
 * @author David F. Houghton - Apr 30, 2012
 * 
 * @param <N>
 */
class AttributeTestExpression<N> implements Expression<N> {
	private final CompiledAttribute<N> a;
	private final Object v;

	enum ComparisonOperator {
		eq, lt, gt, el, eg, id
	}

	enum VType {
		i, f, a, l
	}

	private final AttributeTestExpression.ComparisonOperator c;
	private final AttributeTestExpression.VType vt;

	AttributeTestExpression(Match am, Match vm, Match cm, Forester<N> f) {
		a = new CompiledAttribute<N>(am, f);
		v = CompiledAttribute.parseArgument(vm, f, vm);
		if (v instanceof Integer)
			vt = VType.i;
		else if (v instanceof Double)
			vt = VType.f;
		else if (v instanceof String)
			vt = VType.l;
		else if (v instanceof CompiledAttribute)
			vt = VType.a;
		else
			throw new PathException(
					"the second argument in an attribute test cannot be of type "
							+ v.getClass());
		String s = cm.group();
		if ("=".equals(s))
			c = ComparisonOperator.eq;
		else if (">".equals(s))
			c = ComparisonOperator.gt;
		else if ("<".equals(s))
			c = ComparisonOperator.lt;
		else if (">=".equals(s))
			c = ComparisonOperator.eg;
		else if ("<=".equals(s))
			c = ComparisonOperator.el;
		else if ("==".equals(s))
			c = ComparisonOperator.id;
		else
			throw new PathException("unknown comparison operator " + s);
	}

	@Override
	public boolean test(N n, Collection<N> context, Index<N> i) {
		Object rv = a.apply(n, context, i);
		if (c == ComparisonOperator.id)
			return v == rv;
		if (rv == null) {
			if (v == null)
				return true;
			else
				return false;
		} else if (v == null) {
			return false;
		}
		int comparison = 0;
		switch (vt) {
		case a:
			if (v.equals(rv))
				comparison = 0;
			else if (v instanceof Number && rv instanceof Number)
				comparison = Double.compare(((Number) rv).doubleValue(),
						((Number) v).doubleValue());
			else
				comparison = rv.toString().compareTo(v.toString());
			break;
		case f:
			if (rv instanceof Number)
				comparison = Double.compare(((Number) rv).doubleValue(),
						((Integer) v).doubleValue());
			else
				comparison = rv.toString().compareTo(v.toString());
			break;
		case i:
			if (rv instanceof Integer)
				comparison = ((Integer) rv).compareTo((Integer) v);
			else if (rv instanceof Number)
				comparison = Double.compare(((Number) rv).doubleValue(),
						((Integer) v).doubleValue());
			else if (rv instanceof Collection)
				comparison = ((Collection<?>) rv).size()
						- ((Integer) v).intValue();
			else
				comparison = rv.toString().compareTo(v.toString());
			break;
		case l:
			comparison = rv.toString().compareTo(v.toString());
			break;
		default:
			throw new PathException("unexpected argument type " + vt);
		}
		switch (c) {
		case eg:
			return comparison >= 0;
		case el:
			return comparison <= 0;
		case eq:
			return comparison == 0;
		case gt:
			return comparison > 0;
		case lt:
			return comparison < 0;
		default:
			throw new PathException("unexpected comparison operator " + c);
		}
	}
}