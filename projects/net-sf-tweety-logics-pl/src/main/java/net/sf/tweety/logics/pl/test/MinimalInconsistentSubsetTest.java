package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.pl.DefaultConsistencyTester;
import net.sf.tweety.logics.pl.LingelingEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.CnfSampler;

public class MinimalInconsistentSubsetTest {

	public static void main(String[] args) throws ParserException, IOException{
		PropositionalSignature sig = new PropositionalSignature();
		for(int i = 0; i < 5; i++)
			sig.add(new Proposition("a" + i));
		CnfSampler sampler = new CnfSampler(sig,0.8);
		PlBeliefSet kb = sampler.randomSample(30, 30);
		System.out.println(kb);
		System.out.println();
		
		DefaultConsistencyTester tester = new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling"));
		
		long millis = System.currentTimeMillis();		
		System.out.println(tester.minimalInconsistentSubsets(kb));
		System.out.println(System.currentTimeMillis()-millis);		
	}
}
