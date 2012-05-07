package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import dfh.treepath.Forester;
import dfh.treepath.Path;
import dfh.treepath.test.XMLToy.Element;
import dfh.treepath.test.XMLToy.XMLToyForester;

/**
 * Tests to make sure attribute tests -- @foo = 1, etc. -- work properly.
 * <p>
 * 
 * @author David F. Houghton - Apr 27, 2012
 * 
 */
public class AttributeTestTests {

	@SuppressWarnings("unchecked")
	private static Forester<Element> f = new XMLToyForester();

	@Test
	public void attributeTestTest1() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = f.path("//b[@attr('foo') = 'bar']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest1_2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = f.path("//b['bar' = @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = f.path("//b[@attr('foo') < 'quux']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestTest2_2() {
		Element root = parse("<a><b/><b foo='bar'/></a>");
		Path<Element> p = f.path("//b['quux' > @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void neTest() {
		Element root = parse("<a><b/><b foo='1'/><b foo='2'/></a>");
		Path<Element> p = f.path("//b[@attr('foo') != '1']");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void neTest2() {
		Element root = parse("<a><b/><b foo='1'/><b foo='2'/></a>");
		Path<Element> p = f.path("//b['1' != @attr('foo')]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
	}

	@Test
	public void attributeTestPredicate1() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("//*[not @id = 'foo']");
		List<Element> l = p.select(root);
		assertEquals(6, l.size());
	}

	@Test
	public void attributeTestPredicate2() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("//*[not (@id = 'foo')]");
		List<Element> l = p.select(root);
		assertEquals(6, l.size());
	}

	@Test
	public void attributeTestAnd1() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true and @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestAnd2() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true and @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestAnd3() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false and @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestAnd4() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false and @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestOr1() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true or @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestOr2() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true or @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestOr3() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false or @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestOr4() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false or @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestXOr1() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestXOr2() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true xor @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestXOr3() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestXOr4() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false xor @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestXOr5() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false xor @false xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestXOr6() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false^@false^@true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}
	@Test
	public void attributeTestXOr7() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false ^ @false ^ @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void attributeTestNot1() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(not @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void attributeTestNot2() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(not @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void precedence1() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true and @false or @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void precedence2() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false or @false xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void precedence3() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true and @false xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void precedence4() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@true and @false xor @false)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}

	@Test
	public void precedence5() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false and @true xor @true)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("true", s);
	}

	@Test
	public void precedence6() {
		Element root = parse("<a />");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/*[@log(@false and (@true xor @true))]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("false", s);
	}
}
