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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider;
import net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Implements an approximation algorithm for the Hs inconsistency measure on streams.
 * 
 * @author Matthias Thimm
 */
public class HsInconsistencyMeasurementProcess extends InconsistencyMeasurementProcess<PlFormula>{
	
	/** Configuration key for the signature. */
	public static final String CONFIG_KEY_SIGNATURE = "signature";
	/** Configuration key for the number of populations tried out. */
	public static final String CONFIG_KEY_NUMBEROFPOPULATIONS = "numberOfPopulations";
	/** Key for the configuration map that points to the smoothing factor to be used. if X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by X=X1*smoothingFactor + X2*(1-smoothingFactor).
	 * This value should be between 0 and 1. If it is -1 no smoothing is done (the same as setting
	 * the smoothing factor to 0. */
	public static final String CONFIG_SMOOTHINGFACTOR = "config_smoothingfactor";	
	
	/** The current candidate populations for a hitting set. */
	private Collection<List<PossibleWorld>> hittingSets;
	
	/** The signature of the formulas. */
	private PlSignature sig;
	/** The witness provider used. */
	private ConsistencyWitnessProvider<PlBeliefSet,PlFormula> witnessProvider;
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
	@Override
	protected void init(Map<String, Object> config) {
		this.sig = (PlSignature) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE);
		this.numberOfPopulations = (int) config.get(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS);
		if(config.containsKey(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR))
			this.smoothingFactor = (double) config.get(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR);
		else this.smoothingFactor = -1;
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
	protected double update(PlFormula formula) {
		int newValue = 0;// Integer.MAX_VALUE;
		//for every population
		for(List<PossibleWorld> hs: this.hittingSets){
			// random choice whether an existing world is removed
			// the probability of removal is decreasing in time
			if(!hs.isEmpty() && rand.nextDouble() <= 1-new Double(this.numFormulas)/(this.numFormulas+1)){
				hs.remove(rand.nextInt(hs.size()));
			}			
			boolean satisfied = false;		
			for(PossibleWorld w: hs){
				if(w.satisfies(formula)){
					satisfied = true;
					break;
				}
			}
			// add some model for the new formula
			if(!satisfied){		
				PossibleWorld w = (PossibleWorld)this.witnessProvider.getWitness(formula);
				// arbitrarily set remaining propositions
				PlSignature sig = new PlSignature(this.sig);
				sig.removeAll(formula.getSignature());
				for(Proposition p: sig)
					if(this.rand.nextDouble() < 0.5)
						w.add(p);			
				hs.add(w);
			}		
			newValue += hs.size()-1; 
		}
		newValue = newValue/this.hittingSets.size();
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
		return "HSP";
	}

}
