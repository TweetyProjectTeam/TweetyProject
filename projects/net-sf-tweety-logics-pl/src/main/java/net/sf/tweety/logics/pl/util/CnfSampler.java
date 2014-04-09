package net.sf.tweety.logics.pl.util;

import java.util.Random;

import net.sf.tweety.BeliefBaseSampler;
import net.sf.tweety.Signature;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * A simple sampler for propositional belief bases. This sampler
 * always generates belief bases in CNF, i.e. every formula
 * appearing in the belief base is a disjunction of literals.
 * 
 * @author Matthias Thimm
 */
public class CnfSampler extends BeliefBaseSampler<PlBeliefSet>{

	/** The maximum ratio of variables appearing in a single formula. */
	private double maxVariableRatio;
	
	/**
	 * Creates a new sampler for the given signature.
	 * @param signature
	 * @param maxVariableRatio the maximum ratio (a value between 0 and 1) of variables
	 * of the signature appearing in some formula.
	 */
	public CnfSampler(Signature signature, double maxVariableRatio) {
		super(signature);
		if(!(signature instanceof PropositionalSignature))
			throw new IllegalArgumentException("Signature of type \"PropositionalSignature\" expected. ");
		this.maxVariableRatio = maxVariableRatio;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		Random rand = new Random();
		int length;
		if(maxLength - minLength > 0)
			length = minLength + rand.nextInt(maxLength - minLength);
		else length = minLength;
		while(beliefSet.size() < length){
			beliefSet.add(this.randomFormula());
		}
		return beliefSet;
	}
	
	/**
	 * Samples a random formula (a disjunction of literals).
	 * @return a random formula (a disjunction of literals).
	 */
	public PropositionalFormula randomFormula(){
		PropositionalSignature sig = (PropositionalSignature)this.getSignature();
		Disjunction d = new Disjunction();		
		Random rand = new Random();
		for(Proposition p: sig){
			if(rand.nextDouble() <= this.maxVariableRatio){
				if(rand.nextBoolean())
					d.add(p);
				else d.add(new Negation(p));
			}
			if(d.size()+1 > this.maxVariableRatio * sig.size())
				break;
		}
		// at least one literal should be added
		if(d.isEmpty()){
			if(rand.nextBoolean())
				d.add((Proposition)sig.toArray()[rand.nextInt(sig.size())]);
			else
				d.add(new Negation((Proposition)sig.toArray()[rand.nextInt(sig.size())]));
		}
		return d;
	}

}
