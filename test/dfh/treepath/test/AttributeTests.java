package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import dfh.treepath.Attribute;
import dfh.treepath.Forester;
import dfh.treepath.Index;
import dfh.treepath.Path;
import dfh.treepath.PathException;
import dfh.treepath.test.XMLToy.Element;
import dfh.treepath.test.XMLToy.XMLToyForester;

/**
 * Tests to make sure attributes work properly.
 * <p>
 * 
 * @author David F. Houghton - Apr 27, 2012
 * 
 */
public class AttributeTests {

	@SuppressWarnings("unchecked")
	private static Forester<Element> f = new XMLToyForester();

	// tests to confirm functionality of tree path

	@Test
	public void attributeTest() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = f.path("//b[@attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void logTest() {
		Element root = parse("<a><b foo='1'/><b foo='2'/><b foo='3'/></a>");
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
	public void idAttributeTest() {
		Element root = parse("<a><b id='foo'><c/><c/><c/></b><b id='bar'><c/></b></a>");
		Path<Element> p = f.path("//b[@id = 'foo']/*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
		p = f.path("//b[@id = 'bar']/*");
		l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void attributeTestValueTest() {
		Element root = parse("<a><b id='foo'/></a>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("//b[@log(@id = 'foo')]");
		p.select(root);
		out.close();
		String output = baos.toString().trim();
		assertEquals("true", output);
	}

	@SuppressWarnings("serial")
	@Test
	public void methodSignatureTest1() {
		try {
			new XMLToyForester() {
				@SuppressWarnings("unused")
				@Attribute
				void foo() {
				}
			};
			fail("should have thrown an exception");
		} catch (PathException e) {
			assertTrue(e.getMessage().startsWith("ill-formed attribute"));
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void methodSignatureTest2() {
		try {
			new XMLToyForester() {
				@SuppressWarnings("unused")
				@Attribute
				void foo(Element e, String bar, int i) {
				}
			};
			fail("should have thrown an exception");
		} catch (PathException e) {
			assertTrue(e.getMessage().startsWith(
					"the second parameter for attribute "));
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void methodSignatureTest3() {
		try {
			new XMLToyForester() {
				@SuppressWarnings("unused")
				@Attribute
				void foo(Element e, Collection<Element> bar, int i) {
				}
			};
			fail("should have thrown an exception");
		} catch (PathException e) {
			assertTrue(e.getMessage().startsWith(
					"the third parameter for attribute "));
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void methodSignatureTest4() {
		try {
			new XMLToyForester() {
				@SuppressWarnings("unused")
				@Attribute
				void foo(Element e, Collection<Element> bar, Index<Element> i) {
				}
			};
			fail("should have thrown an exception");
		} catch (PathException e) {
			assertTrue(e.getMessage().startsWith(
					"attribute @foo does not return any value"));
		}
	}

	@Test
	public void definedTest() {
		Element root = parse("<a><b/><b foo='bar' /></a>");
		Path<Element> p = f.path("//b[@defined(@attr('foo'))]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void falseTest() {
		Element root = parse("<a><b/><b foo='bar' /></a>");
		Path<Element> p = f.path("//b[@false]");
		List<Element> l = p.select(root);
		assertEquals(0, l.size());
	}

	@Test
	public void idTest() {
		Element root = parse("<a><b/><b id='bar' /></a>");
		Path<Element> p = f.path("//b[@id = 'bar']");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void indexTest() {
		Element root = parse("<a><b/><b id='bar' /></a>");
		Path<Element> p = f.path("//b[@id = 'bar' and @index = 1]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void rootTest() {
		Element root = parse("<a><b/><b id='bar' /></a>");
		Path<Element> p = f.path("//a[@root]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void sizeTest() {
		Element root = parse("<a><b/><b><c/></b><b><c/><c/></b></a>");
		Path<Element> p = f.path("//b[@size(./*) = 1]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void pickTest() {
		Element root = parse("<a><b/><b><c/></b><b><c/><c/></b></a>");
		Path<Element> p = f.path("//b[@pick(*, 1) = '<c />']");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void leafTest() {
		Element root = parse("<a><b/><b><c/></b><b></b></a>");
		Path<Element> p = f.path("//b[@leaf]");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void nullTest() {
		Element root = parse("<a><b/><b><c/></b><b><c/><c/></b></a>");
		Path<Element> p = f.path("//b[@pick(*, 1) == @null]");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void thisTest() {
		Element root = parse("<a><b/><b id='bar' /></a>");
		Path<Element> p = f.path("//a[@this == @pick(/., 0)]");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void trueTest() {
		Element root = parse("<a><b/><b foo='bar' /></a>");
		Path<Element> p = f.path("//b[@true]");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void uidTest() {
		Element root = parse("<a><b/><c><d/><d id='foo'/></c></a>");
		Path<Element> p = f.path("//*[@id = 'foo'][@log(@uid)]");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("/1/1", s);
	}

	@Test
	public void badAttributeTest() {
		try {
			f.path("/.[@quux]");
		} catch (PathException e) {
			assertTrue(e.getMessage().startsWith("unknown attribute @"));
		}
	}
}
