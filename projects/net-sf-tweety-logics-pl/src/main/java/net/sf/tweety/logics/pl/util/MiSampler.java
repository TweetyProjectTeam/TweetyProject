package net.sf.tweety.logics.pl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.sf.tweety.BeliefBaseSampler;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Generates random propositional belief base with a given
 * inconsistency measure (for the MI inconsistency measure)
 * and of a given size.
 * 
 * NOTE: this sampler is quite simple and only generates
 * MIs of size four and is very demanding on the size of the
 * signature.
 * 
 * @author Matthias Thimm
 */
public class MiSampler extends BeliefBaseSampler<PlBeliefSet>{

	/**
	 * The inconsistency value of the generated belief sets
	 * (wrt. the MI inconsistency measure).
	 */
	private int incvalue;
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the MI inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 */
	public MiSampler(PropositionalSignature signature, int incvalue) {
		super(signature);
		if(incvalue > signature.size()/2)
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
		// first generate MIs
		int num = 0;
		Stack<Proposition> st = new Stack<Proposition>();
		st.addAll((PropositionalSignature)this.getSignature());
		Proposition a,b;
		while(num < this.incvalue){
			a = st.pop();
			b = st.pop();
			formulas.add(new Disjunction(a,b));
			formulas.add(new Disjunction(a,new Negation(b)));
			formulas.add(new Disjunction(new Negation(a),b));
			formulas.add(new Disjunction(new Negation(a),new Negation(b)));
			num++;
		}
		// add remaining formulas
		Random rand = new Random();
		while(num < maxLength){
			Disjunction d = new Disjunction();
			for(Proposition p: st){
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
