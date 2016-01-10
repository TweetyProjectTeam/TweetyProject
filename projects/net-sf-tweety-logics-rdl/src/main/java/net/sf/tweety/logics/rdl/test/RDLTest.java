package net.sf.tweety.logics.rdl.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.parser.RdlParser;
import net.sf.tweety.logics.rdl.semantics.DefaultSequence;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

public class RDLTest {
	
	static List<DefaultRule> createTestSet(RdlParser parser, String... list) throws Exception{
		List<DefaultRule> result = new LinkedList<>();
		for(String str:list)
			result.add((DefaultRule)(DefaultRule)parser.parseFormula(str));
		return result;
	}
	
	static void defaultRuleTest() throws Exception{
		RdlParser parser = new RdlParser();
		DefaultTheory th = parser.parseBeliefBaseFromFile("example_default_theory.txt");
		List<DefaultRule> testset = createTestSet(parser, 
				"Bird(A)::Flies(A)/Flies(A)",
				"::! Swims(A)/Flies(A)",
				":: Flies(B)/Flies(A)",
				"exists X: (Flies(X)) :: Flies(B); !Swims(B)  / Flies(B)",
				"Married(A,B)::Female(A)/Male(B)",
				"Married(A,B)::Female(A);Male(B)/Male(B)",
				"Father(A,B) :: Married(A,C) / Mother(C,B) ",
				"::(forall X: (Male(X)))/(exists Y:(Male(Y)))",
				"exists Z:(Married(X,Z))::Male(X)  / exists Y:(Father(X,Y))");
		System.out.println("\n--isNormal--");
		for(DefaultRule d: testset){
			System.out.print(d);
			System.out.println("\t\t" + (d.isNormal(th) ? "normal" : "not normal"));
		}
		
		System.out.println("\n--getUnboundVariables--");
		for(DefaultRule d: testset){
			Set<Variable> vs=d.getUnboundVariables();
			System.out.print(d+"\t\t{");
			for(Variable v: vs)
				System.out.print(v);
			System.out.println("}");
		}
		
		
	}
	
	static void parserTest() throws Exception{
		RdlParser parser = new RdlParser();
		DefaultTheory th = parser.parseBeliefBaseFromFile("example_default_theory.txt");
		System.out.println(th);
		
		DefaultRule d = (DefaultRule)parser.parseFormula("exists X:(Flies(X))::Flies(B)/Flies(B)");
		System.out.println(d);
	}
	
	static void sequenceTest() throws Exception{
		RdlParser parser = new RdlParser();
		DefaultTheory th = parser.parseBeliefBaseFromFile("example_default_theory.txt");
		DefaultSequence s = new DefaultSequence(th);
		
		System.out.println(s);
		
		s = s.app((DefaultRule)parser.parseFormula("::!Flies(X)/!Flies(X)"));
		
		System.out.println(s);
		
	}

	public static void main(String[] args) throws Exception {
		//parserTest();
		sequenceTest();
		
;	}

}
