package dfh.treepath.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PathGrammarTest.class, PrecedenceTest.class, XMLToy.class,
		BasicTests.class, AttributeTests.class, AxisTests.class,
		FunctionalForesterTest.class, MatchPathTest.class, MixinTest.class })
public class AllTests {

}
