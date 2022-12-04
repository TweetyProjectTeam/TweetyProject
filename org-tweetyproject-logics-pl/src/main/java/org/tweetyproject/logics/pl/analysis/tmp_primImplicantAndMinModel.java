package org.tweetyproject.logics.pl.analysis;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

public class tmp_primImplicantAndMinModel {
	
	public Set<Set<PlFormula>> getPrimeImplicants(Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> allModels, PlBeliefSet forms){
		Set<Set<PlFormula>> primeImplicants = new HashSet<Set<PlFormula>>();
		for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> inter : allModels) {
			Set<PlFormula> newModel = new HashSet<PlFormula>();
			InterpretationSet<Proposition,PlBeliefSet,PlFormula> curr = new PossibleWorld();

			for(PlFormula toDelete : inter) {
				for(Proposition v : inter) {
					if(!v.equals(toDelete))
						curr.add(v);
				}
				if(!curr.satisfies(forms)) {
					newModel.addAll(toDelete.getLiterals());
				}
				
			}
			primeImplicants.add(newModel);
		}
		return primeImplicants;
	}
	
	public Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> getMinModels(Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> f){
		Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> minModels = new HashSet<InterpretationSet<Proposition,PlBeliefSet,PlFormula>>();
		for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> i : f) {
			boolean isMinimal = true;
			for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> j : f) {
				if(i.containsAll(j) && !i.equals(j) && j.size() > 0)
					isMinimal = false;
			}
			if(isMinimal == true) {
				minModels.add(i);
			}
		}
		return minModels;
	}

}
