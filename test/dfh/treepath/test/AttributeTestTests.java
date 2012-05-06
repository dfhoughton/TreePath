package dfh.treepath.test;

import static dfh.treepath.test.XMLToy.parse;
import static org.junit.Assert.assertEquals;

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
}
