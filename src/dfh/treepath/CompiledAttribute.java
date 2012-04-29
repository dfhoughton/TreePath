package dfh.treepath;

import java.lang.reflect.Method;
import java.util.List;

import dfh.grammar.Match;
import dfh.grammar.MatchTest;

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
public class CompiledAttribute<N> {
	private final Method a;
	private final Object[] args;
	private static final MatchTest argTest = new MatchTest() {
		@Override
		public boolean test(Match m) {
			return m.rule().label().id.equals("arg");
		}
	};

	public CompiledAttribute(Match m, Forester<N> f) {
		String s = m.first("aname").group();
		s = s.substring(1);
		a = f.attributes.get(s);
		List<Match> argList = m.children()[1].closest(argTest);
		args = new Object[argList.size()];
		int index = 0;
		for (Match arg : argList) {
			arg = arg.children()[0];
			String type = arg.rule().label().id;
			Object o = null;
			if (type.equals("treepath")) {
				o = f.path(arg);
			} else if (type.equals("attribute")) {
				o = new CompiledAttribute<N>(arg, f);
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
							+ " in attribute " + m.group());
				}
			} else {
				throw new PathException("unknown attribute argument type "
						+ type + " in attribute " + m.group());
			}
			args[index++] = o;
		}
	}

	@SuppressWarnings("unchecked")
	public Object apply(N n, Index<N> i) {
		Object[] ops = new Object[args.length + 2];
		ops[0] = n;
		ops[1] = i;
		int index = 2;
		for (int j = 0; j < args.length; j++, index++) {
			Object o = args[j];
			if (o instanceof CompiledAttribute<?>) {
				CompiledAttribute<N> ca = (CompiledAttribute<N>) o;
				ops[index] = ca.apply(n, i);
			} else if (o instanceof Path<?>) {
				Path<N> p = (Path<N>) o;
				ops[index] = p.select(n, i);
			} else {
				ops[index] = o;
			}
		}
		try {
			return a.invoke(i.f, ops);
		} catch (Exception e) {
			throw new PathException(e);
		}
	}
}
