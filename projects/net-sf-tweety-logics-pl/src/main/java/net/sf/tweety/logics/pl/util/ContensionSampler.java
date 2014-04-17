package net.sf.tweety.logics.pl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.sf.tweety.BeliefBaseSampler;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Generates random propositional belief base with a given
 * inconsistency measure (for the contension inconsistency measure)
 * and of a given size.
 * 
 * @author Matthias Thimm
 */
public class ContensionSampler extends BeliefBaseSampler<PlBeliefSet>{
	
	/**
	 * The inconsistency value of the generated belief sets
	 * (wrt. the contension inconsistency measure).
	 */
	private int incvalue;
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the contension inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 */
	public ContensionSampler(PropositionalSignature signature, int incvalue) {
		super(signature);
		if(incvalue > signature.size())
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		List<Proposition> props = new ArrayList<Proposition>((PropositionalSignature)this.getSignature());
		List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
		// first add contradictoy formulas
		int num = 0;		
		for(Proposition p: props){
			formulas.add(p);
			formulas.add(new Negation(p));
			num++;
			if(num + 1 > this.incvalue)
				break;
		}
		// add some arbitrary formulas that cannot be inconsistent
		Random rand = new Random();
		while(num < maxLength){
			Disjunction d = new Disjunction();
			for(Proposition p: props){
				if(rand.nextBoolean())
					d.add(p);
			}
			if(!formulas.contains(d)){
				num++;
				formulas.add(d);
			}				
		}
		// shuffle
		Collections.shuffle(formulas);
		return new PlBeliefSet(formulas);
	}

}


