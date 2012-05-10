package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import dfh.treepath.Attribute;
import dfh.treepath.Forester;
import dfh.treepath.FunctionalForester;
import dfh.treepath.Index;
import dfh.treepath.NodeTest;
import dfh.treepath.ParentIndex;
import dfh.treepath.Path;
import dfh.treepath.test.XMLToy.Element;

/**
 * This is just provides a version of
 * {@link dfh.treepath.test.XMLToy.XMLToyForester} extending
 * {@link FunctionalForester} rather than {@link Forester} so as to facilitate
 * testing the attributes in the latter.
 * <p>
 * 
 * @author David F. Houghton - May 7, 2012
 * 
 */
public class FunctionalForesterTest {

	/**
	 * A {@link Forester} suitable for interpreting tree paths for trees
	 * returned by {@link FunctionalForesterTest#parse(String)}.
	 * <p>
	 * 
	 * @author David F. Houghton - Apr 28, 2012
	 */
	public static class XMLToyForester extends FunctionalForester<Element> {
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

		@Attribute
		String tag(Element e, Collection<Element> c, Index<Element> i) {
			return e.tag;
		}
	}

	private static XMLToyForester f;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void prepare() {
		f = new XMLToyForester();
	}

	@Test
	public void matchesTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:matches(@tag, '^foo.*')]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void startsWithTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:starts-with(@tag, 'foo')]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void endsWithTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:ends-with(@tag, 'bar')]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void containsTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:contains(@tag, 'ooba')]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void concatTest1() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:concat('a', 'b'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("ab", s);
	}

	@Test
	public void concatTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:concat('a', 'b', 'c'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("abc", s);
	}

	@Test
	public void indexTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:index(@tag, 'b'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("3", s);
	}

	@Test
	public void maxTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:max(3,2,1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("3", s);
	}

	@Test
	public void maxTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:max(3.5,2,1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("3.5", s);
	}

	@Test
	public void minTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:min(3,2,1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1", s);
	}

	@Test
	public void minTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:min(3,2,1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1.5", s);
	}

	@Test
	public void sumTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:sum(3,2,1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("6.5", s);
	}

	@Test
	public void productTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:prod(1,2,3))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("6.0", s);
	}

	@Test
	public void millisTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@u:millis)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertTrue(Pattern.matches("\\d++", s));
	}

	@Test
	public void replaceFirstTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:replace-first('foo','o','e'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("feo", s);
	}

	@Test
	public void replaceAllTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:replace-all('foo','o','e'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("fee", s);
	}

	@Test
	public void replaceTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:replace('foo','o','e'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("fee", s);
	}

	@Test
	public void compareTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:cmp('foo','o') < 0)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void substrTest1() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:substr('foo',1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("oo", s);
	}

	@Test
	public void substrTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:substr('foo',1,2))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("o", s);
	}

	@Test
	public void lengthTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:len('foo'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("3", s);
	}

	@Test
	public void ucTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:uc('foo'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("FOO", s);
	}

	@Test
	public void lcTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:lc('FOO'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("foo", s);
	}

	@Test
	public void ucFirstTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:ucfirst('FOO'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("Foo", s);
	}

	@Test
	public void trimTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:trim(' foo \t'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("foo", s);
	}

	@Test
	public void normalizeWhitespaceTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:nspace(' foo \nbar\t'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("foo bar", s);
	}

	@Test
	public void joinTest1() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:join(',',1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1", s);
	}

	@Test
	public void joinTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:join(',', 1, 'foo'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1,foo", s);
	}

	@Test
	public void joinTest3() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:join(',', 1, @null))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1,null", s);
	}

	@Test
	public void joinTest4() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:join(@null, 1, 2))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1null2", s);
	}

	@Test
	public void absTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:abs(-1) = @m:abs(1))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void ceilTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:ceil(1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("2.0", s);
	}

	@Test
	public void intTest1() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:int(1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1", s);
	}

	@Test
	public void intTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:int(-1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("-1", s);
	}

	@Test
	public void floorTest1() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:floor(-1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("-2.0", s);
	}

	@Test
	public void floorTest2() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:floor(1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("1.0", s);
	}

	@Test
	public void roundTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@m:round(1.5))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("2", s);
	}

	@Test
	public void lookingAtTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:looking-at(@tag, 'foo'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void findTest() {
		Element root = parse("<foobar/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@s:find(@tag, 'ooba'))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void definedTest() {
		Element root = parse("<a><b/><b foo='bar' /></a>");
		Path<Element> p = f.path("//b[@u:def(@attr('foo'))]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

}
