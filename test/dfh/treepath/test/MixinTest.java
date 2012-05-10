package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

import org.junit.Test;

import dfh.treepath.Attribute;
import dfh.treepath.AttributeLibrary;
import dfh.treepath.Forester;
import dfh.treepath.Index;
import dfh.treepath.Path;
import dfh.treepath.test.XMLToy.Element;
import dfh.treepath.test.XMLToy.XMLToyForester;

/**
 * Tests whether the mixin mechanism works.
 * <p>
 * 
 * @author David F. Houghton - May 10, 2012
 * 
 */
public class MixinTest {
	@SuppressWarnings("serial")
	public static class TestLibrary extends AttributeLibrary<Element> {
		public TestLibrary() {
			super();
		}

		@Attribute
		String foo(Element e, Collection<Element> c, Index<Element> i) {
			return "foo";
		}
	}

	@SuppressWarnings({ "serial", "unchecked" })
	@Test
	public void test() {
		Forester<Element> f = new XMLToyForester() {
			{
				mixin(TestLibrary.class);
			}
		};
		Element root = parse("<root/>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		f.setLoggingStream(out);
		Path<Element> p = f.path("/.[@log(@foo)]");
		p.select(root);
		out.close();
		String s = baos.toString().trim();
		assertEquals("foo", s);
	}

}
