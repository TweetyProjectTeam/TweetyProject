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
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Implements the SEE approach ("selective extension enumeration") to
 * determine the set of credulously acceptable arguments of an AF
 * wrt. complete semantics, cf. [Thimm, Cerutti, Vallati; 2020, in preparation].
 * It iteratively calls a SAT solver to discover new acceptable arguments.
 * 
 * @author Matthias Thimm
 *
 */
public class SeeAcceptabilityReasoner extends AbstractAcceptabilityReasoner {

	private SatSolver satSolver;
		
	/**
	 * Creates a new IaqAcceptabilityReasoner.
	 * @param satSolver some SatSolver.
	 */
	public SeeAcceptabilityReasoner(SatSolver satSolver) {
		this.satSolver = satSolver;
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
			beliefSet.add(in.get(a).combineWithOr(out.get(a).combineWithOr(undec.get(a))));
			beliefSet.add((PlFormula)in.get(a).combineWithAnd(out.get(a)).complement());
			beliefSet.add((PlFormula)in.get(a).combineWithAnd(undec.get(a)).complement());
			beliefSet.add((PlFormula)out.get(a).combineWithAnd(undec.get(a)).complement());
		}		
		// an argument is in iff all attackers are out
		for(Argument a: aaf){
			if(aaf.getAttackers(a).isEmpty()){
				beliefSet.add(((PlFormula)in.get(a)));
			}else{
				Collection<PlFormula> attackersAnd = new HashSet<PlFormula>();
				Collection<PlFormula> attackersOr = new HashSet<PlFormula>();
				Collection<PlFormula> attackersNotAnd = new HashSet<PlFormula>();
				Collection<PlFormula> attackersNotOr = new HashSet<PlFormula>();
				for(Argument b: aaf.getAttackers(a)){
					attackersAnd.add(out.get(b));
					attackersOr.add(in.get(b));
					attackersNotAnd.add((PlFormula)in.get(b).complement());
					attackersNotOr.add((PlFormula)out.get(b).complement());
				}
				beliefSet.add(((PlFormula)out.get(a).complement()).combineWithOr(new Disjunction(attackersOr)));
				beliefSet.add(((PlFormula)in.get(a).complement()).combineWithOr(new Conjunction(attackersAnd)));
				beliefSet.add(((PlFormula)undec.get(a).complement()).combineWithOr(new Conjunction(attackersNotAnd)));
				beliefSet.add(((PlFormula)undec.get(a).complement()).combineWithOr(new Disjunction(attackersNotOr)));
			}
		}
		//====================
		Collection<Argument> result = new HashSet<Argument>();
		Disjunction d = new Disjunction();
		for(Argument a: aaf)
			d.add(in.get(a));
		beliefSet.add(d);
		while(!d.isEmpty()) {
			PossibleWorld w =(PossibleWorld) this.satSolver.getWitness(beliefSet);
			if(w == null)
				break;
			else {
				for(Proposition p: w){
					if(p.getName().startsWith("in_")) {
						Argument b = new Argument(p.getName().substring(3));
						result.add(b);
						d.remove(in.get(b));						
					}
				}
			}			
		}		
		return result;
	}	
}
