package net.sf.tweety.logics.pl.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester;
import net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Implements an approximation algorithm for the Hs inconsistency measure on streams.
 * 
 * @author Matthias Thimm
 */
public class HsInconsistencyMeasurementProcess extends InconsistencyMeasurementProcess<PropositionalFormula>{

	/** The probability of removing a world from the candidate hitting set. */
	public static double REMOVAL_PROBABILITY = 0.00001;	
	
	/** Configuration key for the signature. */
	public static final String CONFIG_KEY_SIGNATURE = "signature";
	/** Configuration key for the consistency tester. */
	public static final String CONFIG_KEY_CONSISTENCYTESTER = "consistencyTester";
	
	/** The current candidate for a hitting set. */
	private List<PossibleWorld> hittingSet;
	
	/** The signature of the formulas. */
	private PropositionalSignature sig;
	/** The consistency tester used. */
	private BeliefSetConsistencyTester<PropositionalFormula,PlBeliefSet> tester;
	/** For randomization. */
	private Random rand;
	
	/** Current inconsistency value. */
	private double currentValue;
	/** The number of formulas encountered. */
	private int numFormulas;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#init(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void init(Map<String, Object> config) {
		this.sig = (PropositionalSignature) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE);
		this.tester = (BeliefSetConsistencyTester<PropositionalFormula,PlBeliefSet>) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_CONSISTENCYTESTER);
		this.hittingSet = new LinkedList<PossibleWorld>();
		this.rand = new Random();
		this.currentValue = 0;
		this.numFormulas = 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#update(net.sf.tweety.Formula)
	 */
	@Override
	protected double update(PropositionalFormula formula) {
		// first check if the formula is satisfied
		boolean satisfied = false;		
		for(PossibleWorld w: this.hittingSet){
			if(w.satisfies(formula)){
				satisfied = true;
				break;
			}
		}
		// random choice whether an existing world is removed
		if(!this.hittingSet.isEmpty() && rand.nextDouble() <= HsInconsistencyMeasurementProcess.REMOVAL_PROBABILITY){
			this.hittingSet.remove(rand.nextInt(this.hittingSet.size()));
		}
		// add some model for the new formula
		if(!satisfied){			
			PossibleWorld w = (PossibleWorld)this.tester.getWitness(formula);
			// arbitrarily set remaining propositions
			PropositionalSignature sig = new PropositionalSignature(this.sig);
			sig.removeAll(formula.getSignature());
			for(Proposition p: sig)
				if(this.rand.nextDouble() < 0.5)
					w.add(p);			
			this.hittingSet.add(w);
		}		
		// we take the average of all inconsistency values.
		this.currentValue = (this.currentValue * this.numFormulas + this.hittingSet.size()-1) / ++this.numFormulas; 
		return this.currentValue;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#toString()
	 */
	@Override
	public String toString() {
		return "HS";
	}

}
