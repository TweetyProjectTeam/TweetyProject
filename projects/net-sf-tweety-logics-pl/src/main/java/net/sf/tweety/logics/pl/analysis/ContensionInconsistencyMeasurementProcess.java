package net.sf.tweety.logics.pl.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider;
import net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.semantics.PriestWorld;
import net.sf.tweety.logics.pl.semantics.PriestWorld.TruthValue;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Implements an approximation algorithm for the Contension inconsistency measure on streams.
 * 
 * @author Matthias Thimm
 */
public class ContensionInconsistencyMeasurementProcess extends InconsistencyMeasurementProcess<PropositionalFormula>{

	/** Configuration key for the signature. */
	public static final String CONFIG_KEY_SIGNATURE = "signature";
	/** Configuration key for the consistency tester. */
	public static final String CONFIG_KEY_WITNESSPROVIDER = "witnessProvider";
	/** Configuration key for the number of populations tried out. */
	public static final String CONFIG_KEY_NUMBEROFPOPULATIONS = "numberOfPopulations";
	
	/** The current candidate 3-valued models. */
	private List<PriestWorld> worlds;
	
	/** The signature of the formulas. */
	private PropositionalSignature sig;
	/** The witness provider used. */
	private ConsistencyWitnessProvider<PropositionalFormula> witnessProvider;
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
		this.sig = (PropositionalSignature) config.get(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE);
		this.witnessProvider = (ConsistencyWitnessProvider<PropositionalFormula>) config.get(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER);
		this.numberOfPopulations = (int) config.get(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS);
		this.worlds = new ArrayList<PriestWorld>();
		for(int i = 0; i < this.numberOfPopulations; i++){
			PriestWorld w = new PriestWorld();
			// initialize all assignments to BOTH
			for(Proposition p: this.sig)
				w.set(p, TruthValue.TRUE);
			this.worlds.add(w);			
		}
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
		for(PriestWorld w: this.worlds){
			// random choice whether some variable assignment 
			// is changed from BOTH to TRUE or FALSE
			if(!w.getConflictbase().isEmpty() && rand.nextDouble() <= 1-new Double(this.numFormulas)/(this.numFormulas+1)){
				TruthValue newVal = rand.nextBoolean() ? TruthValue.TRUE : TruthValue.FALSE;
				List<Proposition> lst = new ArrayList<Proposition>(w.getConflictbase());
				w.set(lst.get(rand.nextInt(lst.size())), newVal);				
			}
			// adjust candidate
			if(!w.satisfies(formula)){
				PossibleWorld pw = (PossibleWorld)this.witnessProvider.getWitness(formula);
				for(Proposition p: pw)
					if(w.get(p).equals(TruthValue.FALSE)) w.set(p, TruthValue.BOTH);
				PropositionalSignature sig2 = new PropositionalSignature(formula.getSignature());
				sig2.removeAll(pw);
				for(Proposition p: sig2)
					if(w.get(p).equals(TruthValue.TRUE)) w.set(p, TruthValue.BOTH);				
			}		
			newValue = Math.min(w.getConflictbase().size(), newValue);
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
		return "approx-contension";
	}

}
