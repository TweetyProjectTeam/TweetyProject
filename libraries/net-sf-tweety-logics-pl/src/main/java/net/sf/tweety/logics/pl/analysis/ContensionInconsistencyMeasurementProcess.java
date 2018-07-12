/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
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
	/** Key for the configuration map that points to the smoothing factor to be used. if X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by X=X1*smoothingFactor + X2*(1-smoothingFactor).
	 * This value should be between 0 and 1. If it is -1 no smoothing is done (the same as setting
	 * the smoothing factor to 0. */
	public static final String CONFIG_SMOOTHINGFACTOR = "config_smoothingfactor";	
	
	/** The current candidate 3-valued models. */
	private List<PriestWorld> worlds;
	
	/** The signature of the formulas. */
	private PropositionalSignature sig;
	/** The witness provider used. */
	private ConsistencyWitnessProvider<PropositionalFormula> witnessProvider;
	/** For randomization. */
	private Random rand;
	/** Whether the inconsistency value should be smoothed: if X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by X=X1*smoothingFactor + X2*(1-smoothingFactor).
	 * This value should be between 0 and 1. If it is -1 no smoothing is done. */
	private double smoothingFactor;
	
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
		if(config.containsKey(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR))
			this.smoothingFactor = (double) config.get(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR);
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
		int newValue = 0;
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
			newValue += w.getConflictbase().size();
		}
		newValue = newValue/this.worlds.size();
		this.numFormulas++;
		// do smoothing
		if(this.smoothingFactor != -1)
			this.currentValue = this.currentValue * this.smoothingFactor + newValue * (1-this.smoothingFactor);
		else this.currentValue = newValue;		
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
