package dfh.treepath.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import dfh.grammar.Grammar;
import dfh.grammar.Match;
import dfh.grammar.Matcher;
import dfh.grammar.Options;
import dfh.treepath.Forester;
import dfh.treepath.MatchPath;

/**
 * Some unit tests to make sure {@link MatchPath} is working as intended.
 * <p>
 * 
 * @author David F. Houghton - May 8, 2012
 * 
 */
public class MatchPathTest {

	private static final Forester<Match> f = MatchPath.standard();
	/**
	 * A silly little context free grammar.
	 */
	private static final String[] rules = {
			//
			"S = <NP> <s> <VP> | <S> <s> <PP>",
			"s = /\\s++/",//
			"NP = [ [ <D> <s> ]?+ <NB> | <Pro> ] | <NP> <s> <PP>",//
			"Pro = <nom> | <acc>",//
			"nom = 'I' | 'you' | 'he' | 'she' | 'it' | 'we' | 'they'",//
			"acc = 'me' | 'you' | 'him' | 'her' | 'it' | 'us' | 'them'",//
			"D = <an> | 'the' | <this> | <that>",//
			"an = 'a' | 'an'",//
			"this = 'this' | 'these'",//
			"that = 'that' | 'those'",//
			"NB = <NB> <s> <PP> | [ <AP> <s> ]++ <NB> | <N>",//
			"PP = <P> <s> <NP>",//
			"AP = <AdvP> <s> <AP> | <A>",//
			"A = 'red' | 'green' | 'blue' | 'fat' | 'thin' | 'good' | 'bad'",//
			"N = 'cat' | 'dog' | 'cars' | 'mat' | 'man' | 'moon' | 'Tuesday'",//
			"VP = [ <AuxP> <s> ]?+ [ <AdvP> <s> ]?+ <V> [ <s> <NP> ]?",//
			"AuxP = 'will' | 'would' | 'might' | 'may' | 'should' | 'shall' | 'did'",//
			"AdvP = <Int> <s> <AdvP> | <Adv>",//
			"Int = 'very' | 'extremely' | 'somewhat' | 'rather'",//
			"Adv = 'quickly' | 'sickly' | 'rudely' | 'deeply' | <Int>",//
			"V = <present> | <past>",//
			"present = [ 'run' | 'eat' | 'see' | 'sit' | 'buy' | 'show' ] 's'?+",//
			"past = 'ran' | 'ate' | 'saw' | 'sat' | 'bought' | 'showed'",//
			"P = 'of' | 'on' | 'with' | 'in' | 'to' | 'from' | 'for'"//
	};
	private static Grammar cfg = new Grammar(rules);

	@Test
	public void test1() {
		Matcher m = cfg.matches("I see you");
		Match n = m.match();
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(2, pronouns.size());
		assertEquals("I", pronouns.get(0).group());
		assertEquals("you", pronouns.get(1).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("see", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("see you", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP//NP").select(n);
		assertEquals(1, objects.size());
		assertEquals("you", objects.get(0).group());
		List<Match> subjects = f.path("//NP[not ./ancestor::*[@label = 'VP']]")
				.select(n);
		assertEquals(1, subjects.size());
		assertEquals("I", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~").select(n);
		assertEquals(3, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("I"));
		assertTrue(phraseSet.contains("see you"));
		assertTrue(phraseSet.contains("you"));
	}

	@Test
	public void test2() {
		Matcher m = cfg.matches("the cat sees you");
		Match n = m.match();
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(1, pronouns.size());
		assertEquals("you", pronouns.get(0).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees you", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP/>NP").select(n);
		assertEquals(1, objects.size());
		assertEquals("you", objects.get(0).group());
		List<Match> subjects = f.path(
				"//NP[not ./ancestor::*[@label = 'VP' or @label = 'NP']]")
				.select(n);
		assertEquals(1, subjects.size());
		assertEquals("the cat", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~").select(n);
		assertEquals(3, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("the cat"));
		assertTrue(phraseSet.contains("sees you"));
		assertTrue(phraseSet.contains("you"));
	}

	@Test
	public void test3() {
		Matcher m = cfg.matches("the fat cat sees you");
		Match n = m.match();
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(1, pronouns.size());
		assertEquals("you", pronouns.get(0).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees you", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP/>NP").select(n);
		assertEquals(1, objects.size());
		assertEquals("you", objects.get(0).group());
		List<Match> subjects = f.path(
				"//NP[not ./ancestor::*[@label = 'VP' or @label = 'NP']]")
				.select(n);
		assertEquals(1, subjects.size());
		assertEquals("the fat cat", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~").select(n);
		assertEquals(4, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("the fat cat"));
		assertTrue(phraseSet.contains("sees you"));
		assertTrue(phraseSet.contains("you"));
		assertTrue(phraseSet.contains("fat"));
	}

	@Test
	public void test4() {
		Matcher m = cfg.matches("the very fat cat sees you");
		Match n = m.match();
		assertNotNull(n);
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(1, pronouns.size());
		assertEquals("you", pronouns.get(0).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sees you", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP/>NP").select(n);
		assertEquals(1, objects.size());
		assertEquals("you", objects.get(0).group());
		List<Match> subjects = f.path(
				"//NP[not ./ancestor::*[@label = 'VP' or @label = 'NP']]")
				.select(n);
		assertEquals(1, subjects.size());
		assertEquals("the very fat cat", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~").select(n);
		assertEquals(6, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("the very fat cat"));
		assertTrue(phraseSet.contains("sees you"));
		assertTrue(phraseSet.contains("you"));
		assertTrue(phraseSet.contains("very fat"));
		assertTrue(phraseSet.contains("very"));
		assertTrue(phraseSet.contains("fat"));
	}

	@Test
	public void test5() {
		Matcher m = cfg.matches("I sit");
		Match n = m.match();
		assertNotNull(n);
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(1, pronouns.size());
		assertEquals("I", pronouns.get(0).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sit", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sit", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP/>NP").select(n);
		assertEquals(0, objects.size());
		List<Match> subjects = f.path(
				"//NP[not ./ancestor::*[@label = 'VP' or @label = 'NP']]")
				.select(n);
		assertEquals(1, subjects.size());
		assertEquals("I", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~").select(n);
		assertEquals(2, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("I"));
		assertTrue(phraseSet.contains("sit"));
	}

	@Test
	public void test6() {
		Matcher m = cfg.matches("I sit on you",
				new Options().keepRightmost(true).study(false));
		Match n = m.match();
		if (n == null)
			System.out.println(m.rightmostMatch());
		assertNotNull(n);
		List<Match> pronouns = f.path("//Pro").select(n);
		assertEquals(2, pronouns.size());
		assertEquals("I", pronouns.get(0).group());
		assertEquals("you", pronouns.get(1).group());
		List<Match> verbs = f.path("//V").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sit", verbs.get(0).group());
		List<Match> verbPhrases = f.path("//VP").select(n);
		assertEquals(1, verbs.size());
		assertEquals("sit", verbPhrases.get(0).group());
		List<Match> objects = f.path("//VP/>NP").select(n);
		assertEquals(0, objects.size());
		List<Match> subjects = f.path(
				"//NP[not ./ancestor::*[@s:matches(@label,'[PVN]P')]]").select(
				n);
		assertEquals(1, subjects.size());
		assertEquals("I", subjects.get(0).group());
		List<Match> phrases = f.path("//~P$~[@s:len(@label) > 1]").select(n);
		assertEquals(4, phrases.size());
		Set<String> phraseSet = new HashSet<String>();
		for (Match p : phrases)
			phraseSet.add(p.group());
		assertTrue(phraseSet.contains("I"));
		assertTrue(phraseSet.contains("sit"));
		assertTrue(phraseSet.contains("you"));
		assertTrue(phraseSet.contains("on you"));
	}

	@Test
	public void test7() {
		Grammar g = new Grammar("rule = 'a' not before 'c' 'b'");
		Match m = g.matches("ab").match();
		@SuppressWarnings("unchecked")
		MatchPath mp = new MatchPath();
		List<Match> list = mp.path("//*[@assertion]").select(m);
		assertEquals(1, list.size());
		list = mp.path("//*[@zero]").select(m);
		assertEquals(1, list.size());
		list = mp.path("//*[@zero and @length > 0]").select(m);
		assertEquals(0, list.size());
		list = mp.path("//*").select(m);
		assertEquals(4, list.size());
		Set<String> set = new HashSet<String>();
		for (Match c : list)
			set.add(c.group());
		assertTrue(set.contains("ab"));
		assertTrue(set.contains("a"));
		assertTrue(set.contains("b"));
		assertTrue(set.contains(""));
	}
}
