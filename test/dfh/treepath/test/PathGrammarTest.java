package dfh.treepath.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dfh.grammar.Match;
import dfh.grammar.Matcher;
import dfh.grammar.Options;
import dfh.treepath.PathGrammar;

public class PathGrammarTest {

	@Test
	public void rootTest() {
		Match m = PathGrammar.g.matches("/.").match();
		assertNotNull(m);
	}

	@Test
	public void rootTagTest() {
		Match m = PathGrammar.g.matches("/b",
				new Options().keepRightmost(true).study(false)).match();
		assertNotNull(m);
	}

	@Test
	public void relativePath1() {
		Match m = PathGrammar.g.matches("foo").match();
		assertNotNull(m);
	}

	@Test
	public void relativePath2() {
		Match m = PathGrammar.g.matches("foo/bar").match();
		assertNotNull(m);
	}

	@Test
	public void relativePath3() {
		Match m = PathGrammar.g.matches("foo//bar").match();
		assertNotNull(m);
	}

	@Test
	public void simplePath() {
		Match m = PathGrammar.g.matches("/foo/bar/baz").match();
		assertNotNull(m);
	}

	@Test
	public void wildcardAndIndex() {
		Match m = PathGrammar.g.matches("/foo/*[1]").match();
		assertNotNull(m);
	}

	@Test
	public void anyStep() {
		Match m = PathGrammar.g.matches("/foo//bar").match();
		assertNotNull(m);
	}

	@Test
	public void anyBar() {
		Match m = PathGrammar.g.matches("//bar").match();
		assertNotNull(m);
	}

	@Test
	public void self() {
		Match m = PathGrammar.g.matches(".").match();
		assertNotNull(m);
	}

	@Test
	public void parent() {
		Match m = PathGrammar.g.matches("..").match();
		assertNotNull(m);
	}

	@Test
	public void parentAxis() {
		Match m = PathGrammar.g.matches("../following::foo").match();
		assertNotNull(m);
	}

	@Test
	public void attribute() {
		Match m = PathGrammar.g.matches("//bar[@chorny]").match();
		assertNotNull(m);
	}

	@Test
	public void attributeWithParams1() {
		String s = "foo[@chorny(1)]";
		Matcher m = PathGrammar.g.matches(s);
		Match n = m.match();
		assertNotNull(n);
	}

	@Test
	public void attributeWithParams2() {
		Match m = PathGrammar.g.matches("//bar[@chorny(1, 'foo', //foo)]")
				.match();
		assertNotNull(m);
	}

	@Test
	public void attributeWithFloatParam() {
		String s = "foo[@chorny(1.1)]";
		Matcher m = PathGrammar.g.matches(s);
		Match n = m.match();
		assertNotNull(n);
	}

	@Test
	public void attributeWithAttributeParam() {
		String s = "foo[@chorny(@bar)]";
		Matcher m = PathGrammar.g.matches(s);
		Match n = m.match();
		assertNotNull(n);
	}

	@Test
	public void booleanPredicate1() {
		String s = "//bar[!@foo]";
		Matcher m = PathGrammar.g.matches(s);
		Match n = m.match();
		assertNotNull(n);
	}

	@Test
	public void booleanPredicate2() {
		Match m = PathGrammar.g.matches(
				"//bar[!@foo || following-sibling::quux]").match();
		assertNotNull(m);
	}

	@Test
	public void multiplePaths() {
		Match m = PathGrammar.g.matches(
				"//bar[!@foo || following-sibling::quux]|//corge").match();
		assertNotNull(m);
	}

	@Test
	public void multiplePredicates() {
		Match m = PathGrammar.g.matches("bar[@foo][@quux]").match();
		assertNotNull(m);
	}

	@Test
	public void pattern() {
		Match m = PathGrammar.g.matches("//~bar~//~quux~").match();
		assertNotNull(m);
	}

	@Test
	public void attributeTest1() {
		Match m = PathGrammar.g.matches("/foo[@a = 1]").match();
		assertNotNull(m);
	}

	@Test
	public void attributeTest2() {
		Match m = PathGrammar.g.matches("/foo[@a = @true]").match();
		assertNotNull(m);
	}

	@Test
	public void attributeTest3() {
		Match m = PathGrammar.g.matches("/foo[@a = 'bar']").match();
		assertNotNull(m);
	}

	@Test
	public void attributeTest4() {
		Match m = PathGrammar.g.matches("/foo[@a = 1.5]").match();
		assertNotNull(m);
	}

	@Test
	public void attributeTest5() {
		Match m = PathGrammar.g.matches("/foo[@a >= -1.5]").match();
		assertNotNull(m);
	}
	@Test
	public void attributeTest6() {
		Match m = PathGrammar.g.matches("//b[@attr('foo') = 'bar']").match();
		assertNotNull(m);
	}
}
