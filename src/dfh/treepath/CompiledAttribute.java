/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.treepath.ConditionalPredicate.Expression;

/**
 * A compiled attribute, as opposed to the annotation {@link Attribute}. A
 * {@link CompiledAttribute} contains the {@link Method} corresponding to the
 * method annotated with {@link Attribute}. It also maintains the argument list,
 * if any. Any attributes or paths in the argument list will be evaluated for
 * the relevant node when the attribute value is computed and the actual
 * arguments given to the method will be the returned node collection or
 * attribute return value.
 * <p>
 * 
 * @author David F. Houghton - Apr 28, 2012
 * 
 * @param <N>
 */
class CompiledAttribute<N> {
	private final Method a;
	private final Object[] args;
	private final String name;
	private static final MatchTest argTest = new MatchTest() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("arg");
		}
	};

	CompiledAttribute(Match m, Forester<N> f) {
		String s = m.first("aname").group();
		name = s.substring(1);
		a = f.attributes.get(name);
		if (a == null)
			throw new PathException("unknown attribute @" + name);
		List<Match> argList = m.children()[1].closest(argTest);
		args = new Object[argList.size()];
		int index = 0;
		for (Match arg : argList) {
			Match am = arg.children()[0];
			Object o = parseArgument(m, f, am);
			args[index++] = o;
		}
	}

	/**
	 * Converts various matches into the appropriate argument type.
	 * 
	 * @param master
	 * @param f
	 * @param arg
	 * @return
	 */
	static <N> Object parseArgument(Match master, Forester<N> f, Match arg) {
		String type = arg.rule().label().id;
		Object o = null;
		if (type.equals("treepath")) {
			o = f.path(arg);
		} else if (type.equals("attribute")) {
			o = new CompiledAttribute<N>(arg, f);
		} else if (type.equals("attribute_test")) {
			o = new AttributeTestExpression<N>(arg, f);
		} else if (type.equals("condition")) {
			o = ConditionalPredicate.createExpression(arg, f);
		} else if (type.equals("literal")) {
			String literal = arg.group();
			literal = literal.substring(1, literal.length() - 1);
			literal = literal.replaceAll("\\\\(.)", "$1");
			o = literal;
		} else if (type.equals("num")) {
			arg = arg.children()[0];
			String subtype = arg.rule().label().id;
			if (subtype.equals("signed_int")) {
				o = new Integer(arg.group());
			} else if (subtype.equals("float")) {
				o = new Double(arg.group());
			} else {
				throw new PathException("unknown num subtype: " + subtype
						+ " in attribute " + master.group());
			}
		} else {
			throw new PathException("unknown attribute argument type " + type
					+ " in attribute " + master.group());
		}
		return o;
	}

	Object apply(N n, Collection<N> c, Index<N> i) {
		Object[] ops;
		int varArgsIndex = -1;
		Class<?> arType = null;
		if (a.isVarArgs()) {
			Class<?>[] params = a.getParameterTypes();
			ops = new Object[params.length];
			varArgsIndex = params.length - 1;
			arType = params[varArgsIndex].getComponentType();
		} else
			ops = new Object[args.length + 3];
		ops[0] = n;
		ops[1] = c;
		ops[2] = i;
		int index = 3;
		for (int j = 0; j < args.length; j++, index++) {
			if (index == varArgsIndex) {
				int size = args.length - j;
				Object ar = Array.newInstance(arType, size);
				ops[index] = ar;
				for (int k = 0; j < args.length; j++, k++) {
					Object o = args[j];
					o = objectifyArgument(n, c, i, o);
					Array.set(ar, k, arType.cast(o));
				}
				break;
			}
			Object o = args[j];
			ops[index] = objectifyArgument(n, c, i, o);
		}
		try {
			return a.invoke(i.f, ops);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg == null)
				msg = "check parameters";
			throw new PathException("attribute '" + name
					+ "' failed during application: " + msg);
		}
	}

	@SuppressWarnings("unchecked")
	public Object objectifyArgument(N n, Collection<N> c, Index<N> i, Object o) {
		if (o instanceof CompiledAttribute<?>) {
			CompiledAttribute<N> ca = (CompiledAttribute<N>) o;
			return ca.apply(n, c, i);
		} else if (o instanceof AttributeTestExpression<?>) {
			AttributeTestExpression<N> ate = (AttributeTestExpression<N>) o;
			return ate.test(n, c, i);
		} else if (o instanceof Path<?>) {
			Path<N> p = (Path<N>) o;
			return p.sel(n, i);
		} else if (o instanceof Expression<?>) {
			return ((Expression<N>) o).test(n, c, i);
		} else {
			return o;
		}
	}
}
