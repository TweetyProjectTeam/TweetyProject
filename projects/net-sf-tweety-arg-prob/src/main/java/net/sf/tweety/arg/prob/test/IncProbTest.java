package net.sf.tweety.arg.prob.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.analysis.PAInconsistencyMeasure;
import net.sf.tweety.arg.prob.dynamics.*;
import net.sf.tweety.arg.prob.semantics.*;
import net.sf.tweety.math.func.EntropyFunction;
import net.sf.tweety.math.norm.*;
import net.sf.tweety.math.probability.Probability;

public class IncProbTest {
	public static void main(String[] args){

		DungTheory theory = new DungTheory();
		Argument a = new Argument("A");
		Argument b = new Argument("B");
		Argument c = new Argument("C");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,c));
		theory.add(new Attack(a,c));
		
		PartialProbabilityAssignment ppa = new PartialProbabilityAssignment();
		ppa.put(a, new Probability(0.9));
		
		
		PAInconsistencyMeasure mes = new PAInconsistencyMeasure(new PNorm(2), theory, new SemiOptimisticPASemantics());
		
		System.out.println(mes.inconsistencyMeasure(ppa));
		
		ChangeOperator op = new PAUpdateOperator(new CoherentPASemantics(), new EntropyNorm<Extension>(), new EntropyFunction());
		
		System.out.println(op.change(ppa, theory));
	}
}
