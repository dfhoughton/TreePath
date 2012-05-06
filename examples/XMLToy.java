import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.treepath.Attribute;
import dfh.treepath.Forester;
import dfh.treepath.Index;
import dfh.treepath.ParentIndex;

/**
 * Parser for a simplified form of XML. This is just the identically named class
 * from dfh.treepath.test stripped of testing methods.
 * <p>
 * 
 * @author David F. Houghton - Apr 27, 2012
 * 
 */
public class XMLToy {
	private static String[] rules = {
			//
			"element = <s> [ <simple> | <container> ] <s>",//
			"simple = '<' <tag> <attributes> <s> '/>'",//
			"container = '<' <tag> <attributes> '>' <element>*+ '</' 2 '>'",//
			"tag = /\\w(\\w|:\\w)*+/",//
			"attributes = [ <s> <attribute> ]*+",//
			"attribute = <tag> '=' <quoted>",//
			"quoted = <dquote> | <squote>",//
			"dquote = /\"(?:[^\"]|\\\\.)*+\"/",//
			"squote = /'(?:[^']|\\\\.)*+'/",//
			"s = /\\s*+/",//
	};
	public static final Grammar g = new Grammar(rules);

	/**
	 * A node in an {@link XMLToy} tree.
	 * <p>
	 * 
	 * @author David F. Houghton - Apr 28, 2012
	 * 
	 */
	public static class Element {
		private static final MatchTest closestMT = new MatchTest() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Match m) {
				return m.rule().label().id.equals("element");
			}
		};
		public final String tag;
		public final Map<String, String> attributes;
		public final Element[] children;

		public Element(Match e) {
			tag = e.first("tag").group();
			List<Match> alist = e.first("attributes").get("attribute");
			attributes = new HashMap<String, String>();
			for (Match a : alist) {
				String key = a.first("tag").group();
				String value = a.first("quoted").group();
				value = value.substring(1, value.length() - 1);
				value = value.replaceAll("\\\\(.)", "$1");
				attributes.put(key, value);
			}
			Match m = e.children()[1].children()[0];
			if (m.hasLabel("simple")) {
				children = new Element[0];
			} else {
				List<Match> clist = m.children()[4].closest(closestMT);
				children = new Element[clist.size()];
				for (int i = 0; i < children.length; i++)
					children[i] = new Element(clist.get(i));
			}
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append('<').append(tag);
			b.append(' ');
			for (Entry<String, String> e : attributes.entrySet()) {
				b.append(e.getKey());
				b.append("=\"");
				b.append(e.getValue());
				b.append('"');
			}
			if (children.length == 0)
				b.append("/>");
			else {
				b.append('>');
				for (Element c : children)
					b.append(c);
				b.append("</").append(tag).append('>');
			}
			return b.toString();
		}
	}

	/**
	 * A {@link Forester} suitable for interpreting tree paths for trees
	 * returned by {@link XMLToy#parse(String)}.
	 * <p>
	 * 
	 * @author David F. Houghton - Apr 28, 2012
	 */
	@SuppressWarnings("unchecked")
	public static class XMLToyForester extends Forester<Element> {
		private static final long serialVersionUID = 1L;

		@Override
		public Index<Element> index(Element root) {
			return new ParentIndex<Element>(root, this);
		}

		@Override
		protected List<Element> children(Element n, Index<Element> i) {
			List<Element> children = new ArrayList<Element>(n.children.length);
			for (Element e : n.children)
				children.add(e);
			return children;
		}

		@Override
		protected boolean hasTag(Element n, String tag) {
			return n.tag.equals(tag);
		}

		@Override
		protected boolean matchesTag(Element n, Pattern p) {
			return p.matcher(n.tag).find();
		}

		@Override
		protected Element parent(Element n, Index<Element> i) {
			return ((ParentIndex<Element>) i).parent(n);
		}

		/**
		 * A treepath attribute that returns the value of the specified XML
		 * attribute for the given element.
		 * 
		 * @param e
		 *            context node
		 * @param c
		 *            context collection
		 * @param i
		 *            tree index
		 * @param name
		 *            the attribute of interest
		 * @return the value of the attribute of interest, or null if it is not
		 *         defined for the node
		 */
		@Attribute
		public String attr(Element e, Collection<Element> c, Index<Element> i,
				String name) {
			return e.attributes.get(name);
		}
	}

	/**
	 * Parses toy XML.
	 * 
	 * @param s
	 * @return the root of the XML tree described by s
	 */
	public static Element parse(String s) {
		Match root = g.matches(s).match();
		if (root == null)
			return null;
		return new Element(root);
	}

}
