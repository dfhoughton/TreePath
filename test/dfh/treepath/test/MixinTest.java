package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
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
@SuppressWarnings("serial")
public class MixinTest implements Serializable {
	public static class TestLibrary extends AttributeLibrary<Element> {
		public TestLibrary() {
			super();
		}

		@Attribute
		String foo(Element e, Collection<Element> c, Index<Element> i) {
			return "foo";
		}
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Test
	public void serializationTest() throws IOException, ClassNotFoundException {
		Forester<Element> f = new XMLToyForester() {
			@Override
			protected void init() {
				if (attributes == null) {
					super.init();
					mixin(TestLibrary.class);
				}
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
		baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(f);
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				baos.toByteArray()));
		f = (Forester<Element>) ois.readObject();
		baos = new ByteArrayOutputStream();
		out = new PrintStream(baos);
		f.setLoggingStream(out);
		p = f.path("/.[@log(@foo)]");
		p.select(root);
		out.close();
		s = baos.toString().trim();
		assertEquals("foo", s);
	}

}
