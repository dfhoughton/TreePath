package dfh.treepath.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;

/**
 * Parser for a simplified form of XML. Used to create trees for testing. Also
 * contains the unit tests to ensure it's parsing XML properly.
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
			"tag = /\\w++/",//
			"attributes = [ <s> <attribute> ]*+",//
			"attribute = <tag> '=' <quoted>",//
			"quoted = <dquote> | <squote>",//
			"dquote = /\"(?:[^\"]|\\\\.)*+\"/",//
			"squote = /'(?:[^']|\\\\.)*+'/",//
			"s = /\\s*+/",//
	};
	public static final Grammar g = new Grammar(rules);

	public static class Element {
		private static final MatchTest closestMT = new MatchTest() {
			@Override
			public boolean test(Match m) {
				return m.hasLabel("element");
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
				attributes.put(key, value);
			}
			if (e.children()[1].children()[0].hasLabel("simple")) {
				children = new Element[0];
			} else {
				List<Match> clist = e.children()[1].children()[0].children()[4]
						.closest(closestMT);
				children = new Element[clist.size()];
				for (int i = 0; i < children.length; i++)
					children[i] = new Element(clist.get(i));
			}
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

	@Test
	public void simpleRoot() {
		Element e = parse("<foo/>");
		assertNotNull(e);
		assertEquals("foo", e.tag);
		assertEquals(e.children.length, 0);
	}

	@Test
	public void emptyRoot() {
		Element e = parse("<foo></foo>");
		assertNotNull(e);
		assertEquals("foo", e.tag);
		assertEquals(e.children.length, 0);
	}

	@Test
	public void oneChild() {
		Element e = parse("<foo><bar/></foo>");
		assertNotNull(e);
		assertEquals("foo", e.tag);
		assertEquals(1, e.children.length);
		assertEquals("bar", e.children[0].tag);
	}

	@Test
	public void attributes() {
		Element e = parse("<foo bar='baz' quux='corge' />");
		assertNotNull(e);
		assertEquals("foo", e.tag);
		assertEquals(0, e.children.length);
		assertEquals(2, e.attributes.size());
		assertEquals("baz", e.attributes.get("bar"));
		assertEquals("corge", e.attributes.get("quux"));
	}

	@Test
	public void slightlyComplex() {
		Element e = parse("<a><b /> <c foo='bar'><baz /> <bumpus></bumpus></c></a>");
		assertNotNull(e);
		assertEquals(2, e.children.length);
		assertEquals("b", e.children[0].tag);
		assertEquals("c", e.children[1].tag);
		assertEquals(1, e.children[1].attributes.size());
		assertEquals(2, e.children[1].children.length);
		assertEquals("baz", e.children[1].children[0].tag);
		assertEquals("bumpus", e.children[1].children[1].tag);
	}
}
