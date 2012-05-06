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
 * Tests for the various axes -- child::, preceding::, leaf::, etc.
 * <p>
 * 
 * @author David F. Houghton - Apr 27, 2012
 * 
 */
public class AxisTests {

	@SuppressWarnings("unchecked")
	private static Forester<Element> f = new XMLToyForester();

	@Test
	public void axisTest1() {
		Element root = parse("<a><b/><c><b/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = f.path("//b[@attr('foo') = 'bar']/preceding::b");
		Collection<Element> bs = p.select(root);
		assertEquals(3, bs.size());
	}

	@Test
	public void axisTest2() {
		Element root = parse("<a><b/><c><b quux='corge'/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = f.path("//b[@attr('foo') = 'bar']/preceding::b[1]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
		assertEquals("corge", bs.iterator().next().attributes.get("quux"));
	}

	@Test
	public void axisTest3() {
		Element root = parse("<a><b/><c><b quux='corge'/><d><b/></d></c><b foo='bar'/><b/><c><b/></c></a>");
		Path<Element> p = f.path("//b[@attr('foo') = 'bar']/preceding::b[-2]");
		Collection<Element> bs = p.select(root);
		assertEquals(1, bs.size());
		assertEquals("corge", bs.iterator().next().attributes.get("quux"));
	}

	@Test
	public void axisTestChild() {
		Element root = parse("<a><b/><c/><d/></a>");
		Path<Element> p = f.path("/a/child::*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
	}

	@Test
	public void axisTestAncestor() {
		Element root = parse("<a><b><c><d/></c></b></a>");
		Path<Element> p = f.path("//d/ancestor::*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
	}

	@Test
	public void axisTestAncestorOrSelf() {
		Element root = parse("<a><b><c><d/></c></b></a>");
		Path<Element> p = f.path("//d/ancestor-or-self::*");
		List<Element> l = p.select(root);
		assertEquals(4, l.size());
	}

	@Test
	public void axisTestDescendant() {
		Element root = parse("<a><b><c><d/></c></b></a>");
		Path<Element> p = f.path("//a/descendant::*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
	}

	@Test
	public void axisTestDescendantOrSelf() {
		Element root = parse("<a><b><c><d/></c></b></a>");
		Path<Element> p = f.path("//a/descendant-or-self::*");
		List<Element> l = p.select(root);
		assertEquals(4, l.size());
	}

	@Test
	public void axisTestFollowing() {
		Element root = parse("<a><b><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/following::*");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void axisTestFollowingSibling() {
		Element root = parse("<a><b><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/following-sibling::*");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void axisTestPreceding() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/preceding::*");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void axisTestPrecedingSibling() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/preceding-sibling::*");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
	}

	@Test
	public void axisTestSibling() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/sibling::*");
		List<Element> l = p.select(root);
		assertEquals(2, l.size());
	}

	@Test
	public void axisTestSiblingOrSelf() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/sibling-or-self::*");
		List<Element> l = p.select(root);
		assertEquals(3, l.size());
	}

	@Test
	public void axisTestLeaf() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("/./leaf::*");
		List<Element> l = p.select(root);
		assertEquals(5, l.size());
	}

	@Test
	public void axisTestSelf() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("/./self::*");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
		assertEquals("a", l.get(0).tag);
	}

	@Test
	public void axisTestParent() {
		Element root = parse("<a><e/><b><d/><c id='foo'/><d/></b><e/></a>");
		Path<Element> p = f.path("id(foo)/parent::*");
		List<Element> l = p.select(root);
		assertEquals(1, l.size());
		assertEquals("b", l.get(0).tag);
	}
}
