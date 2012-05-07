package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		Path<Element> p = f.path("/.[@s:matches('^foo.*', @tag)]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void startsWithTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:starts-with('foo', @tag)]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void endsWithTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:ends-with('bar', @tag)]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void containsTest() {
		Element root = parse("<foobar/>");
		Path<Element> p = f.path("/.[@s:contains('ooba', @tag)]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}
}
