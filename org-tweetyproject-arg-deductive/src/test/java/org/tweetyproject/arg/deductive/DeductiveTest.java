package org.tweetyproject.arg.deductive;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.tweetyproject.arg.deductive.accumulator.SimpleAccumulator;
import org.tweetyproject.arg.deductive.categorizer.ClassicalCategorizer;
import org.tweetyproject.arg.deductive.parser.SimplePlLogicParser;
import org.tweetyproject.arg.deductive.reasoner.AbstractDeductiveArgumentationReasoner;
import org.tweetyproject.arg.deductive.reasoner.SimpleDeductiveReasoner;
import org.tweetyproject.arg.deductive.syntax.DeductiveKnowledgeBase;
import org.tweetyproject.arg.deductive.syntax.SimplePlLogicDeductiveKnowledgebase;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.TweetyConfiguration;
import org.tweetyproject.commons.TweetyLogging;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;


public class DeductiveTest {
    @Test
    public void PreferredReasoning() throws ParserException, IOException {
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.TRACE;
		TweetyLogging.initLogging();
		SatSolver.setDefaultSolver(new Sat4jSolver());
		DeductiveKnowledgeBase kb = new DeductiveKnowledgeBase();
		
		PlParser parser = new PlParser();
		kb.add((PlFormula)parser.parseFormula("s"));
		kb.add((PlFormula)parser.parseFormula("!s || h"));
		kb.add((PlFormula)parser.parseFormula("f"));		
		kb.add((PlFormula)parser.parseFormula("!f || !h"));
		kb.add((PlFormula)parser.parseFormula("v"));
		kb.add((PlFormula)parser.parseFormula("!v || !h"));
		

		
		AbstractDeductiveArgumentationReasoner reasoner = new SimpleDeductiveReasoner(new ClassicalCategorizer(), new SimpleAccumulator());
		double result = reasoner.query(kb,(PlFormula) parser.parseFormula("h"));
        assertEquals(result, -2.0, 0.001);
    }
	public void plLogic(String[] args) {
		
		String skb = "a" + "\n" +
					"t" + "\n" +
					"a, t -> b" + "\n" + 
					"b -> c" + "\n" +
					"-> d" + "\n" +
					"d -> !a" + "\n" +
					"d -> !c";
		
		
		SimplePlLogicParser p = new SimplePlLogicParser();
		
		SimplePlLogicDeductiveKnowledgebase k = null;
		try {
			k = p.parseBeliefBase(new StringReader(skb));
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		

		assertEquals(k.getAF().toString(), "<{ <[[a, t] -> b, [b] -> c, t, a],c>, <[[d] -> !a, d],!a>, <[[d] -> !c, d],!c>, <[[a, t] -> b, t, a],b> },[(<[[a, t] -> b, [b] -> c, t, a],c>,<[[d] -> !c, d],!c>), (<[[d] -> !a, d],!a>,<[[a, t] -> b, [b] -> c, t, a],c>), (<[[d] -> !c, d],!c>,<[[a, t] -> b, [b] -> c, t, a],c>), (<[[d] -> !a, d],!a>,<[[a, t] -> b, t, a],b>)]>");

	}
    
}
