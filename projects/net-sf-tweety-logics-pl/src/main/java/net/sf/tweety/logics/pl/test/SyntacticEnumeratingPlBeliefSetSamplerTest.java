package net.sf.tweety.logics.pl.test;

import java.io.File;
import java.io.IOException;

import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.SyntacticEnumeratingPlBeliefSetSampler;

public class SyntacticEnumeratingPlBeliefSetSamplerTest {

	public static void main(String[] args) throws IOException{
		// generates all syntactic variations of propositional belief sets 
		// with 0-3 formulas, each formula has maximal length 4, and 4 propositions		
		PropositionalSignature sig = new PropositionalSignature(4);
		SyntacticEnumeratingPlBeliefSetSampler s = new SyntacticEnumeratingPlBeliefSetSampler(sig,4, new File("/Users/mthimm/Desktop/plfiles/"), false);
		int i = 0;
		while(true){
			System.out.println(i++ + "\t" + s.randomSample(0, 3));			
		}
	}
}
