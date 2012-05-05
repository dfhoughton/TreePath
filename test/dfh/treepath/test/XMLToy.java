package dfh.treepath.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.MatchTest;
import dfh.treepath.Attribute;
import dfh.treepath.Forester;
import dfh.treepath.Index;
import dfh.treepath.NodeTest;
import dfh.treepath.ParentIndex;
import dfh.treepath.Path;

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
	}

	/**
	 * A {@link Forester} suitable for interpreting tree paths for trees
	 * returned by {@link XMLToy#parse(String)}.
	 * <p>
	 * 
	 * @author David F. Houghton - Apr 28, 2012
	 */
	public static class XMLToyForester extends Forester<Element> {
		private static final long serialVersionUID = 1L;

		public XMLToyForester(NodeTest<Element>... nodeTest) {
			super(nodeTest);
		}

		@Override
		public Index<Element> index(Element root) {
			return new ParentIndex<Element>(root, this) {
				@Override
				public String id(Element e) {
					return e.attributes.get("id");
				}
			};
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
		 * @param i
		 * @param name
		 * @return
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

	// tests to confirm functionality of XMLToy

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

	// tests to confirm functionality of tree path

	@Test
	public void anywhereTag() {
		Element root = parse("<a><b/><c><b/><d><b/><b/></d></c></a>");
		Path<Element> p = new XMLToyForester().path("//b");
		Collection<Element> bs = p.select(root);
		assertEquals(4, bs.size());
	}

	@Test
	public void rootTag() {
		Element root = parse("<a><b/><c><b/><d><b/><b/></d></c></a>");
		Forester<Element> f = new XMLToyForester();
		Path<Element> p = f.path("/b");
		Collection<Element> bs = p.select(root);
		assertEquals(0, bs.size());
	}

	@Test
	public void closestTag() {
		Element root = parse("<a><b/><c><b><d><b/></d></b></c></a>");
		Path<Element> p = new XMLToyForester().path("/>b");
		Collection<Element> bs = p.select(root);
		assertEquals(2, bs.size());
	}

	@Test
	public void attributeTest() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = new XMLToyForester().path("//b[@attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void anywhereTest1() {
		Element root = parse("<a><c><d><b/></d></c><b/></a>");
		Path<Element> p = new XMLToyForester().path("//c//b");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void indexTest() {
		Element root = parse("<a><b foo='1'/><b foo='2'/><b foo='3'/></a>");
		Path<Element> p = new XMLToyForester().path("//b[1]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
		assertEquals("2", bs.iterator().next().attributes.get("foo"));
	}

	@Test
	public void attributeTestTest1() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b[@attr('foo') = 'bar']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest1_2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b['bar' = @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b[@attr('foo') < 'quux']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest2_2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b['quux' > @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void axisTest1() {
		Element root = parse("<a><b/><c><b/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b[@attr('foo') = 'bar']/preceding::b");
		Collection<Element> bs = p.select(root);
		assertEquals(3, bs.size());
	}

	@Test
	public void axisTest2() {
		Element root = parse("<a><b/><c><b quux='corge'/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b[@attr('foo') = 'bar']/preceding::b[1]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
		assertEquals("corge", bs.iterator().next().attributes.get("quux"));
	}

	@Test
	public void axisTest3() {
		Element root = parse("<a><b/><c><b quux='corge'/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = new XMLToyForester()
				.path("//b[@attr('foo') = 'bar']/preceding::b[-2]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
		assertEquals("corge", bs.iterator().next().attributes.get("quux"));
	}

	@Test
	public void neTest() {
		Element root = parse("<a><b/><b foo='1'/><b foo='2'/></a>");
		Path<Element> p = new XMLToyForester().path("//b[@attr('foo') != '1']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void neTest2() {
		Element root = parse("<a><b/><b foo='1'/><b foo='2'/></a>");
		Path<Element> p = new XMLToyForester().path("//b['1' != @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void colonTest() {
		Element root = parse("<a:b><b:b/><b:b fo:o='1'/><b:b fo:o='2'/></a:b>");
		Path<Element> p = new XMLToyForester()
				.path("//b:b[@attr('fo:o') != '1']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void logTest() {
		Element root = parse("<a><b foo='1'/><b foo='2'/><b foo='3'/></a>");
		Forester<Element> f = new XMLToyForester();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(baos);
		f.setLoggingStream(stream);
		Path<Element> p = f.path("//b[@log(@attr('foo'))]");
		Collection<Element> bs = p.select(root);
		assertEquals(3, bs.size());
		stream.close();
		String log = new String(baos.toByteArray());
		assertEquals("1\n2\n3\n", log);
	}

	@Test
	public void ignoreTest1() {
		Element root = parse("<a><b/><c/></a>");
		@SuppressWarnings({ "unchecked", "serial" })
		Forester<Element> f = new XMLToyForester(new NodeTest<Element>() {
			@Override
			public boolean passes(Element n, Index<Element> i) {
				return n.tag.equals("c");
			}
		});
		Path<Element> p = f.path("/*");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void reuseTest() {
		Element root = parse("<root><a><b/><c><a/></c></a><b><b><a><c/></a></b></b></root>");
		Forester<Element> f = new XMLToyForester();
		Path<Element> p = f.path("//root");
		Collection<Element> c = p.select(root);
		assertEquals(1, c.size());
		p = f.path("//a");
		c = p.select(root);
		assertEquals(3, c.size());
		p = f.path("//b");
		c = p.select(root);
		assertEquals(3, c.size());
		p = f.path("//c");
		c = p.select(root);
		assertEquals(2, c.size());
		root = parse("<root><c><b><a/></b></c></root>");
		p = f.path("//root");
		c = p.select(root);
		assertEquals(1, c.size());
		p = f.path("//a");
		c = p.select(root);
		assertEquals(1, c.size());
		p = f.path("//b");
		c = p.select(root);
		assertEquals(1, c.size());
		p = f.path("//c");
		c = p.select(root);
		assertEquals(1, c.size());
	}

	@Test
	public void idAttributeTest() {
		Element root = parse("<a><b id='foo'><c/><c/><c/></b><b id='bar'><c/></b></a>");
		Forester<Element> f = new XMLToyForester();
		Path<Element> p = f.path("//b[@id = 'foo']/*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
		p = f.path("//b[@id = 'bar']/*");
		l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void idTest() {
		Element root = parse("<a><b id='foo'><c/><c/><c/></b><b id='bar'><c/></b><b id='(foo)'><c/><c/></b></a>");
		Forester<Element> f = new XMLToyForester();
		Path<Element> p = f.path("id(foo)/*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
		p = f.path("id(bar)/*");
		l = p.select(root);
		assertEquals(1, l.size());
		p = f.path("id(\\(foo\\))/*");
		l = p.select(root);
		assertEquals(2, l.size());
	}
}
