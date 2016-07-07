package net.sf.tweety.arg.aspic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicWord;
import net.sf.tweety.commons.util.rules.DerivationGraph;

public class AspicTest {

	//@Test
	public void test() throws Exception {

		AspicParser parser = new AspicParser();
		String input = "=>s\n=>u \n=>x\n ->p\n->x \n d1: p => q\n p->v \ns=>t\n t=> - d1\nu =>v\nu,x=>- t\n s=> -p\np,q->r\nv->-\ts";
		AspicArgumentationSystem at = parser.parseBeliefBase(input);
		System.out.println(at);
		
	}
	
	@Test
	public void ParserAndDerivationGraphTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "-> a \n => b \n b,c =>d \n a=> e \n b -> e \n e, b-> f";
		AspicArgumentationSystem at = parser.parseBeliefBase(input);
		Collection<AspicInferenceRule> rules = at.getRules();
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
	
	@Test
	public void ArgSysTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "-> a \n => b \n b,c =>d \n a-> e \n b -> e \n e, b=> f \n a, f -> g";
		AspicArgumentationSystem at = parser.parseBeliefBase(input);
		Collection<AspicArgument> args = at.getArguments();
		for(AspicArgument a:args)
			System.out.println(a);
		assertTrue(args.size() == 8);
		for(AspicArgument a:args)
			if(a.getConc() .equals(new AspicWord("f"))
					|| a.getConc() .equals(new AspicWord("g")))
				assertTrue(a.isDefeasible());
			else
				assertFalse(a.isDefeasible());

		
	}
	
	@Test
	public void AttackTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "=> - a \n"
				+ " => a \n"
				+ "-> - b \n"
				+ "-> b \n"
				+ "a,b->c\n";
		AspicArgumentationSystem at = parser.parseBeliefBase(input);
		Collection<AspicArgument> args = at.getArguments();
		
		AspicArgument not_a = new AspicArgument((AspicInferenceRule)parser.parseFormula("=> -a"));
		AspicArgument arg_a = new AspicArgument((AspicInferenceRule)parser.parseFormula("=> a"));
		AspicArgument not_b = new AspicArgument((AspicInferenceRule)parser.parseFormula("-> - b"));
		AspicArgument not_c = new AspicArgument((AspicInferenceRule)parser.parseFormula("-> - c"));
		AspicArgument ab_mapsto_c = new AspicArgument((AspicInferenceRule)parser.parseFormula("a,b->c"));
		assertTrue(args.contains(not_a));
		assertTrue(args.contains(not_b));
		assertFalse(args.contains(not_c));
		assertFalse(args.contains(ab_mapsto_c));
		
		int sum = 0;
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(not_a,arg);
			a.attack();
			System.out.println(a.getOutput());
			if(a.getResult())
				sum++;
		}
		assertTrue(sum==2);
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(not_b,arg);
			a.attack();
			System.out.println(a.getOutput());
			assertFalse(a.getResult());
		}
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(arg_a,arg);
			a.attack();
			System.out.println(a.getOutput());
			if(arg.equals(not_a))
				assertTrue(a.getResult());
			else
				assertFalse(a.getResult());
		}
		
	}
}