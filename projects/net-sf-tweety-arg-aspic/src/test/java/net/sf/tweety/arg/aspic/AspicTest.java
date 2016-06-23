package net.sf.tweety.arg.aspic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicWord;
import net.sf.tweety.commons.util.rules.DerivationGraph;

public class AspicTest {

	//@Test
	public void test() throws Exception {

		AspicParser parser = new AspicParser();
		String input = "=>s\n=>u \n=>x\n ->p\n->x \n d1: p => q\n p->v \ns=>t\n t=> - d1\nu =>v\nu,x=>- t\n s=> -p\np,q->r\nv->-\ts";
		AspicTheory at = parser.parseBeliefBase(input);
		System.out.println(at);
		
	}
	
	@Test
	public void ParserAndDerivationGraphTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "-> a \n => b \n b,c =>d \n a=> e \n b -> e \n e, b-> f";
		AspicTheory at = parser.parseBeliefBase(input);
		Collection<AspicInferenceRule> rules = at.as.getRules();
		assertTrue(rules.size() == 6);
		DerivationGraph<AspicFormula, AspicInferenceRule> g = new DerivationGraph<>();
		g.allDerivations(rules);
		System.out.println(g.getValues());
		assertTrue(g.numberOfNodes()==6);
		for(AspicInferenceRule r:g.getValues())
			assertTrue(rules.contains(r));
		for(AspicInferenceRule r: rules)
			if(r.getConclusion().equals(new AspicWord("d")))
				assertFalse(g.getValues().contains(r));
			else
				assertTrue(g.getValues().contains(r));
		assertTrue(g.numberOfEdges() == 6);
		assertTrue(g.getLeafs().size() == 2);
	}

}