package net.sf.tweety.logics.pl.test;

import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.SyntacticRandomPlBeliefSetSampler;
import net.sf.tweety.math.probability.Probability;

public class SyntacticRandomPlBeliefSetSamplerTest {

	public static void main(String[] args){
		PropositionalSignature sig = new PropositionalSignature(4);
		SyntacticRandomPlBeliefSetSampler sampler = new SyntacticRandomPlBeliefSetSampler(sig,new Probability(0.2),new Probability(0.35),new Probability(0.35),0.5);
		for(int i = 0; i< 10; i++)
			System.out.println(sampler.randomSample(1, 1));
		
	}
}
