package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.BeliefBaseSampler;
import net.sf.tweety.BeliefSet;
import net.sf.tweety.ParserException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.EtaInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.HsInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.CnfSampler;
import net.sf.tweety.logics.pl.util.HsSampler;
import net.sf.tweety.math.opt.solver.LpSolve;

public class InconsistencyTest {

	public static void main(String[] args) throws ParserException, IOException{
		// Create some knowledge base
		PlBeliefSet kb1, kb2;
				
		PropositionalSignature sig = new PropositionalSignature(6);
		BeliefBaseSampler<PlBeliefSet> sampler = new HsSampler(sig,3);
		
		BeliefSetInconsistencyMeasure<PropositionalFormula> hs = new HsInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(sig));
		LpSolve.binary = "/opt/local/bin/lp_solve";
		BeliefSetInconsistencyMeasure<PropositionalFormula> eta = new EtaInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(sig));
		
		double a1,a2,a3,a4;
		for(int i = 0; i<1000; i++){
			kb1 = sampler.randomSample(5, 5);
			kb2 = sampler.randomSample(5, 5);
			
			a1 = hs.inconsistencyMeasure(kb1);
			a2 = hs.inconsistencyMeasure(kb2);
			a3 = eta.inconsistencyMeasure(kb1);
			a4 = eta.inconsistencyMeasure(kb2);
			
			if(!(!(a3 >= a4) || (a1>=a2 ))){
				System.out.println(kb1);
				System.out.println(kb2);
				System.exit(0);
			}else System.out.println(i);
	
		}
	}
}
