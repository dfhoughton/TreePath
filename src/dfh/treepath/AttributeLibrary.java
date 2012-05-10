/*
 * dfh.treepath -- a generic tree querying library for Java
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package dfh.treepath;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The base class all attribute libraries must extend. This is a bit of a hack
 * to implement mixins in Java for this limited case. To create an attribute
 * library to mix in, create a public class extending this class and make sure
 * it has a public zero-argument constructor. Then call
 * {@link Forester#mixin(Class...)}, passing in your class object. You may do
 * this ad hoc, or you may do it in your class like so:
 * 
 * <pre>
 * class Foo&lt;N&gt; extends Forester&lt;N&gt; {
 * 	{
 * 		mixin(Library1.class, Library2.class);
 * 	}
 * 	// ...
 * }
 * </pre>
 * 
 * Now this class will have these attributes mixed in. These attributes will not
 * survive serialization, however. If you wish to ensure your {@link Forester}
 * has these methods after deserialization, you should override its init method
 * like so:
 * 
 * <pre>
 * &#064;Override
 * protected void init() {
 * 	if (attributes == null) {
 * 		super.init();
 * 		mixin(Library1.class, Library2.class);
 * 	}
 * }
 * </pre>
 * <p>
 * Attribute libraries can do the basic work of finding a node's parent or
 * children (by delegating to a more expert forester), but the tag matching
 * methods -- {@link #hasTag(Object, String)} and
 * {@link #matchesTag(Object, Pattern)} -- with throw an error, so don't count
 * on them.
 * <p>
 * 
 * @author David F. Houghton - May 10, 2012
 * 
 * @param <N>
 *            the type of node the library should handle (type erasure is going
 *            to obliterate this)
 */
public class AttributeLibrary<N> extends Forester<N> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public AttributeLibrary() {
		super();
	}

	@Override
	protected List<N> children(N n, Index<N> i) {
		return i.f.children(n, i);
	}

	@Override
	protected boolean hasTag(N n, String tag) {
		throw new UnsupportedOperationException(
				"AttributeLibrary cannot test tags");
	}

	@Override
	protected boolean matchesTag(N n, Pattern p) {
		throw new UnsupportedOperationException(
				"AttributeLibrary cannot match tags");
	}

	@Override
	protected N parent(N n, Index<N> i) {
		return i.f.parent(n, i);
	}

	@Override
	InstanceWrapper wrapMethod(Method m) {
		final AttributeLibrary<N> al = this;
		return new InstanceWrapper(this, m) {
			@SuppressWarnings("rawtypes")
			@Override
			Forester instance(Forester f) {
				return al;
			}
		};
	}
}
