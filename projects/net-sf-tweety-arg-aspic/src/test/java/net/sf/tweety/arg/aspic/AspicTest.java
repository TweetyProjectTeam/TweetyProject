package net.sf.tweety.arg.aspic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Collection;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class AspicTest {

	
	@Test
	public void ParserTest1() throws Exception {
		FolParser folparser = new FolParser();
		String folbsp = "Animal = {horse, cow, lion} \n"
				+ "type(Tame(Animal)) \n"
				+ "type(Ridable(Animal)) \n";
		folparser.parseBeliefBase(folbsp);
		AspicParser<FolFormula> aspicparser = new AspicParser<>(folparser);
		aspicparser.setSymbolComma(";");
		aspicparser.setSymbolDefeasible("==>");
		aspicparser.setSymbolStrict("-->");
		String aspicbsp = "d1: Tame(cow) ==> Ridable(cow)\n"
				+ "s1 : Tame(horse) && Ridable(lion) --> Tame(horse)";
		AspicArgumentationTheory<FolFormula> aat = aspicparser.parseBeliefBase(aspicbsp);
		assertTrue(aat.getRules().size() == 2);
	}
	
	@Test
	public void ParserTest2() throws Exception {
		PlParser plparser = new PlParser();
		AspicParser<FolFormula> aspicparser = new AspicParser<>(plparser);
		aspicparser.setSymbolComma(";");
		aspicparser.setSymbolDefeasible("==>");
		aspicparser.setSymbolStrict("-->");
		String aspicbsp = "d1: a ==> b\n"
				+ "s1 : c; d ==> e \n"
				+ "d ; r --> a";
		AspicArgumentationTheory<FolFormula> aat = aspicparser.parseBeliefBase(aspicbsp);
		assertTrue(aat.getRules().size() == 3);
	}
	
	@Test
	public void DerivationGraphTest() throws Exception {
		AspicParser<PropositionalFormula> parser = new AspicParser<>(new PlParser());
		String input = "-> a \n => b \n b,c =>d \n a=> e \n b -> e \n e, b-> f";
		AspicArgumentationTheory<PropositionalFormula> aat = parser.parseBeliefBase(input);
		Collection<InferenceRule<PropositionalFormula>> rules = aat.getRules();
		assertTrue(rules.size() == 6);
		DerivationGraph<PropositionalFormula, InferenceRule<PropositionalFormula>> g = new DerivationGraph<>();
		g.allDerivations(rules);
		assertTrue(g.numberOfNodes()==6);
		for(InferenceRule<PropositionalFormula> r:g.getValues())
			assertTrue(rules.contains(r));
		for(InferenceRule<PropositionalFormula> r: rules)
			if(r.getConclusion().equals(new PlParser().parseFormula("d")))
				assertFalse(g.getValues().contains(r));
			else
				assertTrue(g.getValues().contains(r));
		assertTrue(g.numberOfEdges() == 6);
		assertTrue(g.getLeafs().size() == 2);
	}
	
/*	//@Test
	public void test() throws Exception {

		AspicParser parser = new AspicParser();
		String input = "=>s\n=>u \n=>x\n ->p\n->x \n d1: p => q\n p->v \ns=>t\n t=> - d1\nu =>v\nu,x=>- t\n s=> -p\np,q->r\nv->-\ts";
		AspicArgumentationTheory at = parser.parseBeliefBase(input);
		System.out.println(at);
		
	}
	
	
	
	@Test
	public void ArgSysTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "-> a \n => b \n b,c =>d \n a-> e \n b -> e \n e, b=> f \n a, f -> g";
		AspicArgumentationTheory at = parser.parseBeliefBase(input);
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
		AspicArgumentationTheory at = parser.parseBeliefBase(input);
		Collection<AspicArgument> args = at.getArguments();
		
		AspicArgument not_a = new AspicArgument((InferenceRule)parser.parseFormula("=> -a"));
		AspicArgument arg_a = new AspicArgument((InferenceRule)parser.parseFormula("=> a"));
		AspicArgument not_b = new AspicArgument((InferenceRule)parser.parseFormula("-> - b"));
		AspicArgument not_c = new AspicArgument((InferenceRule)parser.parseFormula("-> - c"));
		AspicArgument ab_mapsto_c = new AspicArgument((InferenceRule)parser.parseFormula("a,b->c"));
		assertTrue(args.contains(not_a));
		assertTrue(args.contains(not_b));
		assertFalse(args.contains(not_c));
		assertFalse(args.contains(ab_mapsto_c));
		
		int sum = 0;
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(not_a,arg);
			a.attack();
			//System.out.println(a.getOutput());
			if(a.isSuccessfull())
				sum++;
		}
		assertTrue(sum==2);
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(not_b,arg);
			a.attack();
			//System.out.println(a.getOutput());
			assertFalse(a.isSuccessfull());
		}
		for (AspicArgument arg: args) {
			AspicAttack a=new AspicAttack(arg_a,arg);
			a.attack();
			//System.out.println(a.getOutput());
			if(arg.equals(not_a))
				assertTrue(a.isSuccessfull());
			else
				assertFalse(a.isSuccessfull());
		}
		
	}
	
	@Test
	public void SimpleOrderTest() throws Exception {
		AspicParser parser = new AspicParser();
		String input = "=> BornInScotland\n"
				+ " => FitnessLover \n"
				+ "d1: BornInScotland => Scottish \n"
				+ "d2: Scottish => LikesWhiskey \n"
				+ "d3: FitnessLover => - LikesWhiskey\n"
				+ "d1<d3<d2";
		AspicArgumentationTheory at = parser.parseBeliefBase(input);
		Collection<AspicAttack> attacks_w_order = AspicAttack.determineAttackRelations(at.getArguments(), at.getOrder());
		Collection<AspicAttack> attacks_wo_order = AspicAttack.determineAttackRelations(at.getArguments(), null);
		assertTrue(attacks_w_order.size() == 1);
		assertTrue(attacks_wo_order.size() == 2);
	}*/
}