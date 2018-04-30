/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.cl.kappa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.logics.cl.semantics.ConditionalStructure;
import net.sf.tweety.logics.cl.semantics.ConditionalStructure.Generator;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.semantics.NicePossibleWorld;

/**
 * This class builds the initial kappa value list for a given conditional 
 * structure. That means it takes the conditionals of a belief base and generates
 * the for each conditional a {@link KappaValue} instance and also it sub instances
 * of type {@link KappaMin}.
 * 
 * @author Tim Janus
 */
public class ConditionalStructureKappaBuilder {
	
	/** if this flag is true the building of the kappa minimas breaks at the first zero */
	private boolean fastEvaluation;
	
	/** 
	 * Default-Ctor: Generates a builder that does not uses fast evaluation
	 */
	public ConditionalStructureKappaBuilder() {
		this(false);
	}
	
	/**
	 * Ctor: Generates a builder that uses fast evaluation depending on the given parameter
	 * @param fastEval	A flag indicating if fast evaluation shall be used (true) or not (false)
	 */
	public ConditionalStructureKappaBuilder(boolean fastEval) {
		fastEvaluation = fastEval;
	}
	
	/**
	 * This builds a map of conditionals to {@link KappaValue} instances which represent the penalty
	 * term for a falsified conditional
	 * 
	 * @param structure	The conditional structure that is used as basis belief base for the generation
	 * 					of the kappa values.
	 * @return	A map from {@link Conditional} instances to {@link KappaValue} instances that represent
	 * 			the penalty that is given is the key conditional is falsified.
	 */
	public Map<Conditional, KappaValue> build(ConditionalStructure structure) {
		Map<Conditional, KappaValue> reval = new HashMap<Conditional,KappaValue>();
		
		// first instantiate:
		int i=1;
		for(Conditional cond : structure.getConditionals()) {
			reval.put(cond, new KappaValue(i++, cond));
		}
		
		// build the two minimum terms:
		for(KappaValue kv : reval.values()) {
			Conditional curCondition = kv.cond;
			
			// for every verifying world of curCondition we search conditionals that are falsified:
			List<NicePossibleWorld> verifying = structure.getVerifyingWorlds(curCondition);
			buildMinima(structure, reval, curCondition, verifying, kv.positiveMinimum);
			
			// for every falsifying world of curCondition we search conditionals that are also falsified by that world:
			List<NicePossibleWorld> falsifying = structure.getFalsifiyingWorlds(curCondition);
			buildMinima(structure, reval, curCondition, falsifying, kv.negativeMinimum);
		}
		
		return reval;
	}
	
	/**
	 * Helper method: Builds the minimums for the kappa values, a collection of worlds is given which might
	 * be either the verifiying or the falsifiying worlds and for this collection of worlds the corresponding
	 * {@link KappaMin} is given, such that positiveMinimum is calculated using verifying worlds and 
	 * negativeMinumum is using falsifying worlds.
	 * 
	 * @param structure
	 * @param kappaMap
	 * @param curCondition
	 * @param worlds
	 * @param workingMin
	 */
	private void buildMinima(ConditionalStructure structure, Map<Conditional, KappaValue> kappaMap, 
			Conditional curCondition, List<NicePossibleWorld> worlds, KappaMin workingMin) {
		// for each world
		for(NicePossibleWorld world : worlds) {	
			// build the following sum of other kappa values that are falsified by the world:
			KappaSum sum = new KappaSum();
			Map<Conditional, Generator> map = structure.getWorldGenerators(world);
			for(Entry<Conditional, Generator> entry : map.entrySet()) {
				if(entry.getKey() == curCondition) {
					continue;
				}
				
				if(entry.getValue() == Generator.CG_MINUS) {
					sum.elements.add(kappaMap.get(entry.getKey()));
				}
			}
			
			workingMin.elements.add(sum);
			// break condition when using fast evaluation (a minimum containing an empty sum is zero):
			if(fastEvaluation && sum.elements.isEmpty())
				break;
		}
	}

}
