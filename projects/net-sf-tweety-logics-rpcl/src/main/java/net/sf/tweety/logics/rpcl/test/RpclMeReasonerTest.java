package net.sf.tweety.logics.rpcl.test;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rcl.syntax.*;
import net.sf.tweety.logics.rpcl.*;
import net.sf.tweety.logics.rpcl.semantics.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.probability.*;

public class RpclMeReasonerTest {
	public static void main(String[] args){
		Predicate a = new Predicate("a", 1);
		Predicate b = new Predicate("b", 1);
		Constant c1 = new Constant("c1");		
		Constant c2 = new Constant("c2");
		net.sf.tweety.logics.commons.syntax.Variable x = new net.sf.tweety.logics.commons.syntax.Variable("X");
		net.sf.tweety.logics.fol.syntax.FOLAtom atomA = new net.sf.tweety.logics.fol.syntax.FOLAtom(a);
		atomA.addArgument(x);
		net.sf.tweety.logics.fol.syntax.FOLAtom atomB = new net.sf.tweety.logics.fol.syntax.FOLAtom(b);
		atomB.addArgument(x);
		RelationalProbabilisticConditional pc = new RelationalProbabilisticConditional(atomA,atomB,new Probability(0.3));
		
		RpclBeliefSet bs = new RpclBeliefSet();
		bs.add(pc);
		
		FolSignature sig = new FolSignature();
		sig.add(a);
		sig.add(b);
		sig.add(c1);
		sig.add(c2);
				
		RpclMeReasoner reasoner = new RpclMeReasoner(bs, new AggregatingSemantics(),sig);
		
		
		System.out.println(reasoner.getMeDistribution());
		
		net.sf.tweety.logics.fol.syntax.FOLAtom atomAC = new net.sf.tweety.logics.fol.syntax.FOLAtom(a);
		atomAC.addArgument(c1);
		net.sf.tweety.logics.fol.syntax.FOLAtom atomBC = new net.sf.tweety.logics.fol.syntax.FOLAtom(b);
		atomBC.addArgument(c1);
		
		System.out.println(reasoner.query(new RelationalConditional(atomAC,atomBC)));
		
		
	}
}
