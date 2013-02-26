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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A generic {@link Forester} filled with useful {@link Attribute attributes}
 * (these are the functions). Basically, this {@link Forester} holds a bunch of
 * functions, mostly copied from XPath. If your {@link Forester} extends this
 * class, you get all these functions as well.
 * <p>
 * To reduce the incidence of namespace collisions, all the attributes in this
 * class have namespace prefixes and indicate their category of function:
 * <dl>
 * <dt>s</dt>
 * <dd>String function</dd>
 * <dt>m</dt>
 * <dd>mathematical function</dd>
 * <dt>u</dt>
 * <dd>utility function</dd>
 * </dl>
 * 
 * @author David F. Houghton - May 7, 2012
 * 
 * @param <N>
 *            the type of node in the tree
 */
public abstract class FunctionalForester<N> extends Forester<N> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a funtional forester that ignores the specified node types.
	 * 
	 * @param nodeTest
	 *            nodes to ignore
	 */
	@SuppressWarnings("unchecked")
	public FunctionalForester(NodeTest<N>... nodeTest) {
	}

	/**
	 * Determines whether the pattern matches the entirety of the input string.
	 * This method calls {@link Pattern#matches(String, CharSequence)}. If you
	 * have to match a particular pattern often, it is likely more efficient to
	 * write your own attribute that calls a pre-compiled {@link Pattern}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string matched against
	 * @param pattern
	 *            pattern to match
	 * @return whether the pattern matched
	 */
	@Attribute(value = "s:matches", description = "whether the string parameter matches a pattern")
	protected boolean matches(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return Pattern.matches(pattern, s);
	}

	/**
	 * Calls {@link String#startsWith(String)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to search in
	 * @param pattern
	 *            prefix
	 * @return whether the string has the given prefix
	 */
	@Attribute(value = "s:starts-with", description = "whether the string parameters as a particular prefix")
	protected boolean startsWith(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.startsWith(pattern);
	}

	/**
	 * Calls {@link String#endsWith(String)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to search in
	 * @param pattern
	 *            suffix
	 * @return whether the string has the given suffix
	 */
	@Attribute(value = "s:ends-with", description = "whether the string parameter has a particular suffix")
	protected boolean endsWith(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.endsWith(pattern);
	}

	/**
	 * Calls {@link String#indexOf(int)} and returns whether the value is
	 * greater than -1.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to search in
	 * @param pattern
	 *            infix
	 * @return whether the string contains the given infix
	 */
	@Attribute(value = "s:contains", description = "whether the string contains a particular infix")
	protected boolean contains(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.indexOf(pattern) > -1;
	}

	/**
	 * Calls {@link String#indexOf(String)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to search in
	 * @param pattern
	 *            infix
	 * @return index of first character of infix in string
	 */
	@Attribute(value = "s:index", description = "the index of an infix in the string")
	protected int index(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return s.indexOf(pattern);
	}

	/**
	 * Joins together the stringifications of a list of items in a new string.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param parts
	 *            items to concatenate
	 * @return concatenation of the stringification of an arbitrary list of
	 *         objects
	 */
	@Attribute(value = "s:concat", description = "string concatenating items")
	protected String concatenate(N n, Collection<N> c, Index<N> i,
			Object... parts) {
		StringBuilder b = new StringBuilder();
		for (Object o : parts)
			b.append(o);
		return b.toString();
	}

	/**
	 * Returns the maximum value in a list of numbers.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param nums
	 *            a list of numbers
	 * @return the maximum value in the list
	 */
	@Attribute(value = "m:max", description = "maximum value")
	protected Number max(N n, Collection<N> c, Index<N> i, Number... nums) {
		Number max = nums[0];
		for (int j = 1; j < nums.length; j++) {
			Number n2 = nums[j];
			if (max.doubleValue() < n2.doubleValue())
				max = n2;
		}
		return max;
	}

	/**
	 * Returns the minimum value in a list of numbers.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param nums
	 *            a list of numbers
	 * @return the minimum value
	 */
	@Attribute(value = "m:min", description = "minimum value")
	protected Number min(N n, Collection<N> c, Index<N> i, Number... nums) {
		Number min = nums[0];
		for (int j = 1; j < nums.length; j++) {
			Number n2 = nums[j];
			if (min.doubleValue() > n2.doubleValue())
				min = n2;
		}
		return min;
	}

	/**
	 * Sums the numbers in a list, returning the sum as a double.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param nums
	 *            a list of numbers
	 * @return the sum of values in the list represented as a double
	 */
	@Attribute(value = "m:sum", description = "sum of values")
	protected Double sum(N n, Collection<N> c, Index<N> i, Number... nums) {
		double sum = 0;
		for (Number num : nums)
			sum += num.doubleValue();
		return sum;
	}

	/**
	 * Multiplies the numbers in a list, returning the product as a double. An
	 * empty list returns 0.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param nums
	 *            a list of numbers
	 * @return the product
	 */
	@Attribute(value = "m:prod", description = "product of values")
	protected Double product(N n, Collection<N> c, Index<N> i, Number... nums) {
		if (nums.length == 0)
			return 0D;
		double product = 1;
		for (Number num : nums)
			product *= num.doubleValue();
		return product;
	}

	/**
	 * Returns {@link System#currentTimeMillis()}. Potentially useful in
	 * debugging.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @return {@link System#currentTimeMillis()}
	 */
	@Attribute(value = "u:millis", description = "current time in milliseconds")
	protected Long millis(N n, Collection<N> c, Index<N> i) {
		return System.currentTimeMillis();
	}

	/**
	 * Calls {@link String#replaceFirst(String, String)}
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param pattern
	 *            regular expression
	 * @param replacement
	 *            replacement pattern
	 * @return transformed string
	 */
	@Attribute(value = "s:replace-first", description = "replace first occurrence of pattern")
	protected String replaceFirst(N n, Collection<N> c, Index<N> i, String s,
			String pattern, String replacement) {
		return s.replaceFirst(pattern, replacement);
	}

	/**
	 * Calls {@link String#replaceAll(String, String)}
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param pattern
	 *            regular expression
	 * @param replacement
	 *            replacement pattern
	 * @return transformed string
	 */
	@Attribute(value = "s:replace-all", description = "replace all occurrences of pattern")
	protected String replaceAll(N n, Collection<N> c, Index<N> i, String s,
			String pattern, String replacement) {
		return s.replaceAll(pattern, replacement);
	}

	/**
	 * Calls {@link String#replace(String, String)}. This is more efficient than
	 * {@link #replaceAll(Object, Collection, Index, String, String, String)}
	 * because it doesn't involve any regular expression compilation.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to modify
	 * @param pattern
	 *            infix
	 * @param replacement
	 *            replacement infix
	 * @return transformed string
	 */
	@Attribute(value = "s:replace", description = "replace all occurrences of infix")
	protected String replace(N n, Collection<N> c, Index<N> i, String s,
			String pattern, String replacement) {
		return s.replace(pattern, replacement);
	}

	/**
	 * Calls {@link String#compareTo(String)}
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s1
	 *            first string
	 * @param s2
	 *            second string
	 * @return integer representing the alphabetical ordering of the two strings
	 */
	@Attribute(value = "s:cmp", description = "compare string order")
	protected Integer compare(N n, Collection<N> c, Index<N> i, String s1,
			String s2) {
		return s1.compareTo(s2);
	}

	/**
	 * Calls {@link String#substring(int, int)} or {@link String#substring(int)}
	 * , depending on how many integer parameters are provided.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to obtain a substring from
	 * @param offsets
	 *            one or two character offsets
	 * @return a substring
	 */
	@Attribute(value = "s:substr", description = "select substring")
	protected String substring(N n, Collection<N> c, Index<N> i, String s,
			Integer... offsets) {
		switch (offsets.length) {
		case 0:
			throw new PathException(
					"s:substr requires at least one offset parameter");
		case 1:
			return s.substring(offsets[0]);
		case 2:
			return s.substring(offsets[0], offsets[1]);
		default:
			throw new PathException(
					"s:substr can take at most two offset parameter");
		}
	}

	/**
	 * Calls {@link String#length()}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to measure
	 * @return the length of the string parameter
	 */
	@Attribute(value = "s:len", description = "string length")
	protected Integer length(N n, Collection<N> c, Index<N> i, String s) {
		return s.length();
	}

	/**
	 * Calls {@link String#toUpperCase()}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to transform
	 * @return string in uppercase
	 */
	@Attribute(value = "s:uc", description = "uppercase")
	protected String uppercase(N n, Collection<N> c, Index<N> i, String s) {
		return s.toUpperCase();
	}

	/**
	 * Calls {@link String#toLowerCase()}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to transform
	 * @return string in lowercase
	 */
	@Attribute(value = "s:lc", description = "lowercase")
	protected String lowercase(N n, Collection<N> c, Index<N> i, String s) {
		return s.toLowerCase();
	}

	/**
	 * Capitalizes the first letter in the string.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to transform
	 * @return initial uppercase string
	 */
	@Attribute(value = "s:ucfirst", description = "capitalize only first letter")
	protected String ucFirst(N n, Collection<N> c, Index<N> i, String s) {
		switch (s.length()) {
		case 0:
			return s;
		case 1:
			return s.toUpperCase();
		default:
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
	}

	/**
	 * Calls {@link String#trim()}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to transform
	 * @return trim without marginal whitespace
	 */
	@Attribute(value = "s:trim", description = "trim whitespace")
	protected String trim(N n, Collection<N> c, Index<N> i, String s) {
		return s.trim();
	}

	/**
	 * Calls {@link String#trim()} and then replaces all internal whitespace
	 * with a single space.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string to transform
	 * @return trim without marginal whitespace and with internal whitespace
	 *         normalized to a single space
	 */
	@Attribute(value = "s:nspace", description = "normalize whitespace")
	protected String normalizeWhitespace(N n, Collection<N> c, Index<N> i,
			String s) {
		return s.trim().replaceAll("\\s++", " ");
	}

	/**
	 * Returns the stringification of a list of items, separating each pair of
	 * items with the specified separator.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param separator
	 *            object whose stringification should be placed between any two
	 *            items
	 * @param items
	 *            items to join
	 * @return trim without marginal whitespace
	 */
	@Attribute(value = "s:join", description = "concatenate items with separator")
	protected String join(N n, Collection<N> c, Index<N> i, Object separator,
			Object... items) {
		// to handle null properly
		String sep = separator == null ? "null" : separator.toString();
		StringBuilder b = new StringBuilder();
		boolean first = true;
		for (Object o : items) {
			if (first)
				first = false;
			else
				b.append(sep);
			b.append(o);
		}
		return b.toString();
	}

	/**
	 * Calls {@link Math#abs(int)} or {@link Math#abs(double)}, attempting to
	 * preserve the precision of the argument.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param num
	 *            number to obtain the absolute value of
	 * @return absolute value of number
	 */
	@Attribute(value = "m:abs", description = "absolute value")
	protected Number abs(N n, Collection<N> c, Index<N> i, Number num) {
		// attempt to preserve type
		return num.intValue() == num.doubleValue() ? Math.abs(num.intValue())
				: Math.abs(num.doubleValue());
	}

	/**
	 * Calls {@link Math#ceil(double)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param num
	 *            number to obtain the ceiling value of
	 * @return ceiling of num
	 */
	@Attribute(value = "m:ceil", description = "round up")
	protected Number ceil(N n, Collection<N> c, Index<N> i, Number num) {
		// attempt to preserve type
		return Math.ceil(num.doubleValue());
	}

	/**
	 * Calls {@link Number#intValue()}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param num
	 *            number to obtain the integral portion of
	 * @return integral portion of num
	 */
	@Attribute(value = "m:int", description = "integral portion")
	protected Integer integralPortion(N n, Collection<N> c, Index<N> i,
			Number num) {
		return num.intValue();
	}

	/**
	 * Calls {@link Math#floor(double)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param num
	 *            number to obtain the floor value of
	 * @return floor of num
	 */
	@Attribute(value = "m:floor", description = "round down")
	protected Number floor(N n, Collection<N> c, Index<N> i, Number num) {
		return Math.floor(num.doubleValue());
	}

	/**
	 * Calls {@link Math#round(double)}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param num
	 *            number to round
	 * @return num rounded to the nearest whole number
	 */
	@Attribute(value = "m:round", description = "round to nearest whole number")
	protected Number round(N n, Collection<N> c, Index<N> i, Number num) {
		return Math.round(num.doubleValue());
	}

	/**
	 * Determines whether the pattern matches the beginning of the input string.
	 * This method calls {@link Matcher#lookingAt()}. If you have to match a
	 * particular pattern often, it is likely more efficient to write your own
	 * attribute that uses a pre-compiled {@link Pattern}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string matched against
	 * @param pattern
	 *            pattern to match
	 * @return whether the pattern matched
	 */
	@Attribute(value = "s:looking-at", description = "match prefix")
	protected boolean lookingAt(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return Pattern.compile(pattern).matcher(s).lookingAt();
	}

	/**
	 * Determines whether the pattern matches the input string anywhere. This
	 * method calls {@link Matcher#find()}. If you have to match a particular
	 * pattern often, it is likely more efficient to write your own attribute
	 * that uses a pre-compiled {@link Pattern}.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index; required for method signature but ignored
	 * @param s
	 *            string matched against
	 * @param pattern
	 *            pattern to match
	 * @return whether the pattern matched
	 */
	@Attribute(value = "s:find", description = "look for pattern in string")
	protected boolean find(N n, Collection<N> c, Index<N> i, String s,
			String pattern) {
		return Pattern.compile(pattern).matcher(s).find();
	}

	/**
	 * A boolean attribute that is true if the object parameter is not null.
	 * 
	 * @param n
	 *            context node; required for method signature but ignored
	 * @param c
	 *            collection of which context node is a member; required for
	 *            method signature but ignored
	 * @param i
	 *            tree index
	 * @param o
	 *            value to test
	 * @return whether o isn't null
	 */
	@Attribute(value = "u:def", description = "whether the parameter value is non-null")
	protected final Boolean defined(N n, Collection<N> c, Index<N> i, Object o) {
		return o == null ? false : true;
	}

}
