package net.sf.tweety.logics.pl.analysis;

import java.util.ArrayList;
import java.util.Collection;
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
	
	/** Configuration key for the signature. */
	public static final String CONFIG_KEY_SIGNATURE = "signature";
	/** Configuration key for the consistency tester. */
	public static final String CONFIG_KEY_CONSISTENCYTESTER = "consistencyTester";
	/** Configuration key for the number of populations tried out. */
	public static final String CONFIG_KEY_NUMBEROFPOPULATIONS = "numberOfPopulations";
	
	/** The current candidate populations for a hitting set. */
	private Collection<List<PossibleWorld>> hittingSets;
	
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
	/** The number of populations. */
	private int numberOfPopulations;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#init(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void init(Map<String, Object> config) {
		this.sig = (PropositionalSignature) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE);
		this.tester = (BeliefSetConsistencyTester<PropositionalFormula,PlBeliefSet>) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_CONSISTENCYTESTER);
		this.numberOfPopulations = (int) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS);
		this.hittingSets = new ArrayList<List<PossibleWorld>>();
		for(int i = 0; i < this.numberOfPopulations; i++)
			this.hittingSets.add(new LinkedList<PossibleWorld>());
		this.rand = new Random();
		this.currentValue = 0;
		this.numFormulas = 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#update(net.sf.tweety.Formula)
	 */
	@Override
	protected double update(PropositionalFormula formula) {
		int newValue = Integer.MAX_VALUE;
		//for every population
		for(List<PossibleWorld> hs: this.hittingSets){
			// first check if the formula is satisfied
			boolean satisfied = false;		
			for(PossibleWorld w: hs){
				if(w.satisfies(formula)){
					satisfied = true;
					break;
				}
			}
			// random choice whether an existing world is removed
			// the probability of removal is decreasing in time
			if(!hs.isEmpty() && rand.nextDouble() <= 1-new Double(this.numFormulas)/(this.numFormulas+1)){
				hs.remove(rand.nextInt(hs.size()));
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
				hs.add(w);		
			}		
			newValue = Math.min(hs.size()-1, newValue);
		}
		// we take the average of all inconsistency values.		
		this.currentValue = (this.currentValue * this.numFormulas + newValue) / ++this.numFormulas;		
		return this.currentValue > 0 ? this.currentValue : 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#toString()
	 */
	@Override
	public String toString() {
		return "HS";
	}

}
