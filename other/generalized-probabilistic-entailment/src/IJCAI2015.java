

import java.util.Collection;
import java.util.LinkedList;

import edu.cs.ai.log4KR.logical.semantics.Interpretation;
import edu.cs.ai.log4KR.logical.semantics.PossibleWorldFactory;
import edu.cs.ai.log4KR.logical.syntax.probabilistic.Conditional;
import edu.cs.ai.log4KR.logical.syntax.probabilistic.Fact;
import edu.cs.ai.log4KR.propositional.classicalLogic.semantics.PropositionalPossibleWorld64BitRepresentationFactory;
import edu.cs.ai.log4KR.propositional.classicalLogic.syntax.BooleanVariable;
import edu.cs.ai.log4KR.propositional.classicalLogic.syntax.PropositionalVariable;
import edu.cs.ai.log4KR.propositional.probabilisticConditionalLogic.kbParser.log4KRReader.Log4KRReader;
import edu.cs.ai.log4KR.structuredLogics.PropositionalSemantics;
import edu.cs.ai.log4KR.structuredLogics.StructuredSemantics;
import edu.cs.ai.log4KR.structuredLogics.inconsistency.reasoning.AbstractGeneralizedEntailmentComponent;
import edu.cs.ai.log4KR.structuredLogics.inconsistency.reasoning.GeneralizedEntailment1Norm;
import edu.cs.ai.log4KR.structuredLogics.inconsistency.reasoning.GeneralizedEntailment2Norm;
import edu.cs.ai.log4KR.structuredLogics.inconsistency.reasoning.GeneralizedEntailmentMaxNorm;

/**
 * Examples from 'Nico Potyka, Matthias Thimm: Probabilistic Reasoning with
 * Inconsistent Beliefs using Inconsistency Measures', IJCAI 2015.
 * 
 * @author NicoPotyka
 *
 */
public class IJCAI2015 {

	public static void main(String[] args) {

		example1();
		example2();
		example3();

	}
	
	
	/**
	 * Different expert opinions on the probability of some event A.
	 */
	public static void example1() {

		System.out.println("\n\nCompute Example 1\n\n");
		
		//initialize knowledge base
		Log4KRReader reader = new Log4KRReader();
		reader.readFromString(""
				+ "signature"
				+ "    A"
				+ " "
				
				+ "conditionals"
				
				+ "  kb1 {"
				+ "    (A)[0],"
				+ "    (A)[1]"
				+ "  }\n"
				+ "  kb2 {"
				+ "    (A)[0.1],"
				+ "    (A)[0.8]"
				+ "  }\n"
				+ "  kb3 {"
				+ "    (A)[0.1],"
				+ "    (A)[0.8],"
				+ "    (A)[0.7]"
				+ "  }\n"
				+ "  kb4 {"
				+ "    (A)[0.1],"
				+ "    (A)[0.8],"
				+ "    (A)[0.9]"
				+ "  }\n"
				+ "  kb5 {"
				+ "    (A)[0.1],"
				+ "    (A)[0.8],"
				+ "    (A)[0.8]"
				+ "  }\n"
		);
		
		
		//knowledge bases to read
		LinkedList<String> kbStrings = new LinkedList<>();
		kbStrings.add("kb1");
		kbStrings.add("kb2");
		kbStrings.add("kb3");
		kbStrings.add("kb4");
		kbStrings.add("kb5");
		
		
		//initialize logical components
		PossibleWorldFactory<PropositionalVariable> worldFactory =  new PropositionalPossibleWorld64BitRepresentationFactory();
		Interpretation<PropositionalVariable>[] possibleWorlds = worldFactory.createPossibleWorlds(reader.getVariables());
		StructuredSemantics<PropositionalVariable> semantics = new  PropositionalSemantics();

		
		//initialize generalized entailment components
		LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>> entComps = new LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>>();
		entComps.add(new GeneralizedEntailment1Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailment2Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailmentMaxNorm<PropositionalVariable>(semantics));
		
		
		//initialize queries
		LinkedList<Conditional<PropositionalVariable>> queries = new LinkedList<>();
		queries.add(new Fact<PropositionalVariable>(new BooleanVariable("A")));
		
		
		//for each knowledge base, perform generalized entailment and answer queries
		for(String kb: kbStrings) {
			
			//print knowledge base
			System.out.println(kb+" = {");
			for(Conditional<PropositionalVariable> c: reader.getKnowledgeBase(kb)) {
				System.out.println("\t"+c);
			}
			System.out.println("}");
			System.out.println();

			//print entailment result
			for(AbstractGeneralizedEntailmentComponent<PropositionalVariable> ent: entComps) {
				System.out.println(ent);
				ent.initialize(possibleWorlds, reader.getKnowledgeBase(kb));
				for(Conditional<PropositionalVariable> query: queries) {
					System.out.println("P"+query+": "+ent.queryProbability(query));
				}
				System.out.println("\n\n");
			}
			

			System.out.println("---------------------------------------------------------------------------------------------\n\n");
			
		}
	}

	

	/**
	 * Nixon diamond
	 */
	public static void example2() {

		System.out.println("\n\nCompute Example 2\n\n");

		//initialize knowledge base
		Log4KRReader reader = new Log4KRReader();
		reader.readFromString(""
				+ "signature"
				+ "    quaker,republican,pacifist,nixon"
				+ " "
				
				+ "conditionals"
				
				+ "  nixonDiamond {"
				+ "    (pacifist|quaker)[0.9],"
				+ "    (pacifist|republican)[0.1],"
				+ "    (republican and quaker |nixon)[1]"
				+ "  }\n"
				
				+ "  integrityConstraints {"
				+ "    (nixon)[1]"
				+ "  }\n"
				
		);
		LinkedList<String> kbStrings = new LinkedList<>();
		kbStrings.add("nixonDiamond");
		
		
		//initialize logical components
		PossibleWorldFactory<PropositionalVariable> worldFactory =  new PropositionalPossibleWorld64BitRepresentationFactory();
		Interpretation<PropositionalVariable>[] possibleWorlds = worldFactory.createPossibleWorlds(reader.getVariables());
		StructuredSemantics<PropositionalVariable> semantics = new  PropositionalSemantics();


		//initialize generalized entailment components
		LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>> entComps = new LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>>();
		entComps.add(new GeneralizedEntailment1Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailment2Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailmentMaxNorm<PropositionalVariable>(semantics));
		
		
		//initialize queries
		LinkedList<Conditional<PropositionalVariable>> queries = new LinkedList<>();
		queries.add(new Fact<PropositionalVariable>(new BooleanVariable("nixon")));
		queries.add(new Conditional<PropositionalVariable>(new BooleanVariable("pacifist"), new BooleanVariable("quaker")));
		queries.add(new Conditional<PropositionalVariable>(new BooleanVariable("pacifist"), new BooleanVariable("republican")));
		queries.add(new Conditional<PropositionalVariable>(new BooleanVariable("pacifist"), new BooleanVariable("nixon")));
		
		
		//for each knowledge base, perform generalized entailment and answer queries
		for(String kb: kbStrings) {
			System.out.println(kb+" = {");
			for(Conditional<PropositionalVariable> c: reader.getKnowledgeBase(kb)) {
				System.out.println("\t"+c);
			}
			System.out.println("}");
			System.out.println();
			

			for(AbstractGeneralizedEntailmentComponent<PropositionalVariable> ent: entComps) {
				System.out.println(ent);
				ent.initialize(possibleWorlds, reader.getKnowledgeBase(kb),reader.getKnowledgeBase("integrityConstraints"));
				for(Conditional<PropositionalVariable> query: queries) {
					System.out.println("P"+query+": "+ent.queryProbability(query));
				}
				System.out.println("\n\n");
			}
			

			System.out.println("---------------------------------------------------------------------------------------------\n\n");
			
		}
	}
	
	
	/**
	 * Lottery examples
	 */
	public static void example3() {

		System.out.println("\n\nCompute Example 3\n\n");
		
		
		//number of players
		int n = 4;
		
		//initialize knowledge base
		StringBuffer sb = new StringBuffer();
		
		sb.append("signature\n");
		
		for(int i=0; i<n; i++) {
			sb.append("w"+i);
			if(i<n-1) sb.append(",");
		}
		
		
		sb.append("\nconditionals\n");
		
		sb.append("lotteryParadox {\n");
		for(int i=0; i<n; i++) {
			
			sb.append("(w"+i+")[0]");
			
			if(i<n-1) sb.append(",");
			
			sb.append("\n");
			
		}		
		sb.append("\n}");
		
		sb.append("integrityConstraints {\n");
		sb.append("(");
		for(int i=0; i<n; i++) {
			sb.append("w"+i);
			if(i<n-1) sb.append(" or ");
		}
		sb.append(")[1]\n}");
		
		
		
		Log4KRReader reader = new Log4KRReader();
		reader.readFromString(sb.toString());
		
		
		//initialize logical components
		PossibleWorldFactory<PropositionalVariable> worldFactory =  new PropositionalPossibleWorld64BitRepresentationFactory();
		Interpretation<PropositionalVariable>[] possibleWorlds = worldFactory.createPossibleWorlds(reader.getVariables());
		StructuredSemantics<PropositionalVariable> semantics = new  PropositionalSemantics();


		//initialize generalized entailment components
		LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>> entComps = new LinkedList<AbstractGeneralizedEntailmentComponent<PropositionalVariable>>();
		entComps.add(new GeneralizedEntailment1Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailment2Norm<PropositionalVariable>(semantics));
		entComps.add(new GeneralizedEntailmentMaxNorm<PropositionalVariable>(semantics));
		
		
		//initialize queries
		LinkedList<Conditional<PropositionalVariable>> queries = new LinkedList<>();
		for(int i=0; i<n; i++) {
			queries.add(new Fact<PropositionalVariable>(new BooleanVariable("w"+i)));
		}
		
		
		//perform generalized entailment and answer queries
		Collection<Conditional<PropositionalVariable>> kb = reader.getKnowledgeBase("lotteryParadox");
		Collection<Conditional<PropositionalVariable>> ic = reader.getKnowledgeBase("integrityConstraints");
		System.out.println("lotteryParadox = {");
		for(Conditional<PropositionalVariable> c:kb) {
			System.out.println("\t"+c);
		}
		System.out.println("}");
		System.out.println("integrityConstraints = {");
		for(Conditional<PropositionalVariable> c:ic) {
			System.out.println("\t"+c);
		}
		System.out.println("}");
		System.out.println();
		
		System.out.println();
		

		for(AbstractGeneralizedEntailmentComponent<PropositionalVariable> ent: entComps) {
			System.out.println(ent);
			ent.initialize(possibleWorlds, kb, ic);
			for(Conditional<PropositionalVariable> query: queries) {
				System.out.println("P"+query+": "+ent.queryProbability(query));
			}
			System.out.println("\n\n");
		}
	}
	
}
