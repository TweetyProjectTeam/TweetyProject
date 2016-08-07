package net.sf.tweety.arg.aspic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import ruleformulagenerator.FolFormulaGenerator;
import ruleformulagenerator.PlFormulaGenerator;

/**
 * @author Nils Geilen
 * Several JUnit test for the package arg.aspic
 */
public class AspicTest {
	
	@Test public void ComplementTest() throws Exception {
		PropositionalFormula f = new Proposition("a");
		assertTrue(f.equals(f.complement().complement()));
	}

	
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
	
	
	
	@Test
	public void ArgSysTest() throws Exception {
		AspicParser<PropositionalFormula> parser = new AspicParser<>(new PlParser());
		String input = "-> a \n => b \n b,c =>d \n a-> e \n b -> e \n e, b=> f \n a, f -> g";
		AspicArgumentationTheory<PropositionalFormula> at = parser.parseBeliefBase(input);
		Collection<AspicArgument<PropositionalFormula>> args = at.getArguments();
		//for(AspicArgument<PropositionalFormula> a:args)
			//System.out.println(a);
		assertTrue(args.size() == 8);
		for(AspicArgument<PropositionalFormula> a:args)
			if(a.getConc() .equals(new Proposition("f"))
					|| a.getConc() .equals(new Proposition("g")))
				assertTrue(a.hasDefeasibleSub());
			else
				assertFalse(a.hasDefeasibleSub());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void AttackTest() throws Exception {
		AspicParser<PropositionalFormula> parser = new AspicParser<>(new PlParser());
		String input = "=> ! a \n"
				+ " => a \n"
				+ "-> ! b \n"
				+ "-> b \n"
				+ "a,b->c\n";
		AspicArgumentationTheory<PropositionalFormula> at = parser.parseBeliefBase(input);
		Collection<AspicArgument<PropositionalFormula>> args = at.getArguments();
		
		AspicArgument<PropositionalFormula> not_a = new AspicArgument<>((InferenceRule<PropositionalFormula>)parser.parseFormula("=> ! a"));
		AspicArgument<PropositionalFormula> arg_a = new AspicArgument<>((InferenceRule<PropositionalFormula>)parser.parseFormula("=> a"));
		AspicArgument<PropositionalFormula> not_b = new AspicArgument<>((InferenceRule<PropositionalFormula>)parser.parseFormula("-> ! b"));
		AspicArgument<PropositionalFormula> not_c = new AspicArgument<>((InferenceRule<PropositionalFormula>)parser.parseFormula("-> ! c"));
		AspicArgument<PropositionalFormula> ab_mapsto_c = new AspicArgument<>((InferenceRule<PropositionalFormula>)parser.parseFormula("a,b->c"));
		assertTrue(args.contains(not_a));
		assertTrue(args.contains(not_b));
		assertFalse(args.contains(not_c));
		assertFalse(args.contains(ab_mapsto_c));
		
		int sum = 0;
		for (AspicArgument<PropositionalFormula> arg: args) {
			AspicAttack<PropositionalFormula> a=new AspicAttack<PropositionalFormula>(not_a,arg);
			a.attack();
			//System.out.println(a.getOutput());
			if(a.isSuccessfull())
				sum++;
		}
		assertTrue(sum==2);
		for (AspicArgument<PropositionalFormula> arg: args) {
			AspicAttack<PropositionalFormula> a=new AspicAttack<PropositionalFormula>(not_b,arg);
			a.attack();
			//System.out.println(a.getOutput());
			assertFalse(a.isSuccessfull());
		}
		for (AspicArgument<PropositionalFormula> arg: args) {
			AspicAttack<PropositionalFormula> a=new AspicAttack<PropositionalFormula>(arg_a,arg);
			a.attack();
			//System.out.println(a.getOutput());
			if(arg.equals(not_a))
				assertTrue(a.isSuccessfull());
			else
				assertFalse(a.isSuccessfull());
		}
		
	}
	
	final PlFormulaGenerator pfg = new PlFormulaGenerator();
	final FolFormulaGenerator folfg = new FolFormulaGenerator();
	
	@SuppressWarnings("unchecked")
	@Test public void PropositionalFormulaGeneratorTest() throws Exception {
		AspicParser<PropositionalFormula> parser = new AspicParser<>(new PlParser());
		String input = "-> a \n"
				+ "d1: a => b \n"
				+ "d2: a => !d1 \n"
				+ "s1: a -> e \n"
				+ "s2: a -> !s1";
		AspicArgumentationTheory<PropositionalFormula> at = parser.parseBeliefBase(input);
		at.setRuleFormulaGenerator(pfg);
		DungTheory dt = at.asDungTheory();
		assertTrue(dt.getAttacks().size() == 1);
		assertTrue(((AspicArgument<PropositionalFormula>)dt.getAttacks().iterator().next().getAttacked()).getConc().equals(new Proposition("b")));
	}
	
	@SuppressWarnings("unchecked")
	@Test public void FolFormulaGeneratorTest() throws Exception {
		FolParser parser = new FolParser();
		String kb = "Rule = {d1,d2,s1,s2} \n"
				+ "type(a) \n type(b) \n type(c) \n type(e) \n"
				+ "type(__rule(Rule)) \n"
				;
		parser.parseBeliefBase(kb);
		AspicParser<FolFormula> aspicparser = new AspicParser<>(parser);
		String input = "-> a \n"
				+ "d1: a => b \n"
				+ "d2: a => !__rule(d1) \n"
				+ "s1: a -> e \n"
				+ "s2: a -> !__rule(s1)";
		AspicArgumentationTheory<FolFormula> at = aspicparser.parseBeliefBase(input);
		at.setRuleFormulaGenerator(folfg);
		DungTheory dt = at.asDungTheory();
		assertTrue(dt.getAttacks().size() == 1);
		assertTrue(((AspicArgument<FolFormula>)dt.getAttacks().iterator().next().getAttacked()).getConc().equals(new FOLAtom(new Predicate("b"))));
	}
	
	@Test
	public void SimpleOrderTest() throws Exception {
		AspicParser<PropositionalFormula> parser = new AspicParser<>(new PlParser());
		String input = "=> BornInScotland\n"
				+ " => FitnessLover \n"
				+ "d1: BornInScotland => Scottish \n"
				+ "d2: Scottish => LikesWhiskey \n"
				+ "d3: FitnessLover => ! LikesWhiskey\n"
				+ "d1<d3<d2";
		AspicArgumentationTheory<PropositionalFormula> at = parser.parseBeliefBase(input);
		assertTrue(at.getRules().size() == 5);
		Collection<AspicArgument<PropositionalFormula>> args = at.getArguments();
		Collection<AspicAttack<PropositionalFormula>> attacks_w_order = AspicAttack.determineAttackRelations(args, at.getOrder(), pfg);
		Collection<AspicAttack<PropositionalFormula>> attacks_wo_order = AspicAttack.determineAttackRelations(args, null, pfg);
		assertTrue(attacks_w_order.size() == 1);
		assertTrue(attacks_wo_order.size() == 2);
	}
}