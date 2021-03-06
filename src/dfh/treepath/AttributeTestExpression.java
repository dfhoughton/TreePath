/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.util.Collection;
import java.util.Iterator;

import dfh.grammar.Match;
import dfh.treepath.ConditionalPredicate.Expression;

/**
 * An expression that evaluates the various attribute test expressions:
 * {@code @foo = 'bar'}, {@code bar < @quux}, etc.
 * <p>
 * 
 * @author David F. Houghton - Apr 30, 2012
 * 
 * @param <N>
 */
class AttributeTestExpression<N> implements Expression<N> {
	private static final long serialVersionUID = 1L;
	private final CompiledAttribute<N> a;
	private final Object v;

	enum ComparisonOperator {
		eq, lt, gt, el, eg, id, ne
	}

	enum VType {
		/**
		 * integer
		 */
		i,
		/**
		 * float
		 */
		f,
		/**
		 * attribute
		 */
		a,
		/**
		 * attribute test
		 */
		at,
		/**
		 * path
		 */
		p,
		/**
		 * literal
		 */
		l
	}

	private final AttributeTestExpression.ComparisonOperator c;
	private final AttributeTestExpression.VType vt;
	private final boolean swapped;

	AttributeTestExpression(Match master, Forester<N> f) {
		master = master.children()[0];
		Match left = master.children()[0], cm = master.children()[2], right = master
				.children()[4], am, vm;
		if (left.rule().label().id.equals("attribute")) {
			am = left;
			vm = right;
			swapped = false;
		} else {
			am = right;
			vm = left;
			swapped = true;
		}
		vm = vm.children()[0];
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
			c = swapped ? ComparisonOperator.lt : ComparisonOperator.gt;
		else if ("<".equals(s))
			c = swapped ? ComparisonOperator.gt : ComparisonOperator.lt;
		else if (">=".equals(s))
			c = swapped ? ComparisonOperator.el : ComparisonOperator.eg;
		else if ("<=".equals(s))
			c = swapped ? ComparisonOperator.eg : ComparisonOperator.el;
		else if ("==".equals(s))
			c = ComparisonOperator.id;
		else if ("!=".equals(s))
			c = ComparisonOperator.ne;
		else
			throw new PathException("unknown comparison operator " + s);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean test(N n, Collection<N> context, Index<N> i) {
		Object rv = a.apply(n, context, i);
		if (c == ComparisonOperator.id) {
			Object o = v;
			if (vt == VType.a) {
				o = ((CompiledAttribute<N>) o).apply(n, context, i);
				if (rv instanceof Collection<?> && o instanceof Collection<?>) {
					Collection<N> c1 = (Collection<N>) rv, c2 = (Collection<N>) o;
					if (c1.size() == c2.size()) {
						for (Iterator<N> i1 = c1.iterator(), i2 = c2.iterator(); i1
								.hasNext();) {
							N n1 = i1.next(), n2 = i2.next();
							if (n1 != n2)
								return false;
						}
						return true;
					}
					return false;
				}
			}
			return o == rv;
		}
		if (rv == null) {
			return false;
		} else if (v == null) {
			return c == ComparisonOperator.ne ? true : false;
		}
		int comparison = 0;
		switch (vt) {
		case a:
			Object o = ((CompiledAttribute<N>) v).apply(n, context, i);
			if (o.equals(rv))
				comparison = 0;
			else if (o instanceof Number && rv instanceof Number)
				comparison = Double.compare(((Number) rv).doubleValue(),
						((Number) o).doubleValue());
			else if (o instanceof Collection<?> && rv instanceof Collection<?>) {
				Collection<N> c1 = (Collection<N>) rv, c2 = (Collection<N>) o;
				if (c1.size() == c2.size()) {
					for (Iterator<N> i1 = c1.iterator(), i2 = c2.iterator(); i1
							.hasNext();) {
						N n1 = i1.next(), n2 = i2.next();
						if (n1 != n2)
							return c == ComparisonOperator.ne ? true : false;
					}
					return c == ComparisonOperator.eq ? true : false;
				} else {
					return c == ComparisonOperator.ne ? true : false;
				}
			} else
				comparison = rv.toString().compareTo(o.toString());
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
		case ne:
			return comparison != 0;
		default:
			throw new PathException("unexpected comparison operator " + c);
		}
	}
}