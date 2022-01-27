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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Implements the SEEM approach ("selective extension enumeration with MaxSAT") to
 * determine the set of credulously acceptable arguments of an AF
 * wrt. complete semantics, cf. [Thimm, Cerutti, Vallati; COMMA 2020].
 * It iteratively calls a MaxSAT solver to discover new acceptable arguments.
 * 
 * @author Matthias Thimm
 *
 */
public class SeemAcceptabilityReasoner extends AbstractAcceptabilityReasoner {

	private MaxSatSolver maxSatSolver;
		
	private Semantics semantics;
	
	private InferenceMode inferenceMode;
	
	/**
	 * Creates a new IaqAcceptabilityReasoner.
	 * @param maxSatSolver some MaxSATSolver.
	 * @param semantics either Semantics.CO, Semantics.PR, or Semantics.ST
	 * @param inferenceMode either InferenceMode.CREDULOUS or InferenceMode.SKEPTICAL (only stable semantics)
	 */
	public SeemAcceptabilityReasoner(MaxSatSolver maxSatSolver,  Semantics semantics, InferenceMode inferenceMode) {
		if(!semantics.equals(Semantics.CO) && !semantics.equals(Semantics.PR) && !semantics.equals(Semantics.ST))
			throw new IllegalArgumentException("Semantics must be CO, PR, or ST");
		if(inferenceMode.equals(InferenceMode.SKEPTICAL) && !semantics.equals(Semantics.ST))
			throw new IllegalArgumentException("Skeptical reasoning only supported for stable semantics");
		this.maxSatSolver = maxSatSolver;
		this.semantics = semantics;
		this.inferenceMode = inferenceMode;
	}
	
	@Override
	public Collection<Argument> getAcceptableArguments(DungTheory aaf) {
		Map<Argument,Proposition> in = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> out = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> undec = new HashMap<Argument,Proposition>();
		PlBeliefSet beliefSet = new PlBeliefSet();
		for(Argument a: aaf){
			in.put(a, new Proposition("in_" + a.getName()));
			out.put(a, new Proposition("out_" + a.getName()));
			undec.put(a, new Proposition("undec_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			beliefSet.add(in.get(a).combineWithOr(out.get(a)).combineWithOr(undec.get(a)));
			beliefSet.add((PlFormula)in.get(a).complement().combineWithOr(out.get(a).complement()));
			beliefSet.add((PlFormula)in.get(a).complement().combineWithOr(undec.get(a).complement()));
			beliefSet.add((PlFormula)out.get(a).complement().combineWithOr(undec.get(a).complement()));
		}		
		// an argument is in iff all attackers are out
		for(Argument a: aaf){
			if(aaf.getAttackers(a).isEmpty()){
				beliefSet.add(((PlFormula)in.get(a)));
			}else{
				Collection<PlFormula> attackersOr = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersNotOr = new HashSet<PlFormula>();//new Contradiction();
				for(Argument b: aaf.getAttackers(a)){
					attackersOr.add(in.get(b));
					attackersNotOr.add((PlFormula)out.get(b).complement());					
					beliefSet.add(((PlFormula)in.get(a).complement()).combineWithOr((PlFormula)out.get(b)));
				}
				beliefSet.add(new Disjunction(attackersOr).combineWithOr((PlFormula)out.get(a).complement()));
				beliefSet.add(new Disjunction(attackersNotOr).combineWithOr((PlFormula)in.get(a)));		
				// for stable semantics, no argument can be undec
				if(this.semantics.equals(Semantics.ST)) {
					beliefSet.add((PlFormula)undec.get(a).complement());
				}
			}
		}
		//====================
		Collection<Argument> result = new HashSet<Argument>();
		if(this.inferenceMode.equals(InferenceMode.CREDULOUS)) {
			Map<PlFormula,Integer> softConstraints = new HashMap<>();
			for(Argument a: aaf)
				softConstraints.put(in.get(a),1);
			boolean changed;
			while(!softConstraints.isEmpty()) {
				PossibleWorld w =(PossibleWorld) this.maxSatSolver.getWitness(beliefSet,softConstraints);
				if(w == null)
					break;
				else {
					changed = false;
					for(Proposition p: w){
						if(p.getName().startsWith("in_")) {
							Argument b = new Argument(p.getName().substring(3));
							result.add(b);												
							if(softConstraints.containsKey(in.get(b))) {
								softConstraints.remove(in.get(b));
								changed = true;
							}							
						}
					}				
					if(!changed) break;
				}			
			}		
		}else {
			Map<PlFormula,Integer> softConstraints = new HashMap<>();
			for(Argument a: aaf) {
				softConstraints.put(out.get(a),1);
				result.add(a);
			}
			boolean changed;
			while(!softConstraints.isEmpty()) {
				PossibleWorld w =(PossibleWorld) this.maxSatSolver.getWitness(beliefSet,softConstraints);
				if(w == null)
					break;
				else {
					changed = false;
					for(Proposition p: w){
						if(p.getName().startsWith("out_")) {
							Argument b = new Argument(p.getName().substring(4));
							result.remove(b);												
							if(softConstraints.containsKey(out.get(b))) {
								softConstraints.remove(out.get(b));
								changed = true;
							}							
						}
					}				
					if(!changed) break;
				}			
			}
		}
		return result;
	}	
	
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return this.maxSatSolver.isInstalled();
	}
}
