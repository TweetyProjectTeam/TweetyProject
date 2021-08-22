package org.tweetyproject.arg.dung.reasoner;


import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;

public class SimpleClPreferredReaonser {
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {
		Semantics co = Semantics.CO;
		ClaimBasedInheritedReasoner reasoner = new ClaimBasedInheritedReasoner(co);
		Set<Set<String>> completeExtensions = reasoner.getModels(bbase);
		Set<Set<String>> result = new HashSet<Set<String>>();
		boolean maximal;
		for(Set<String> e1: completeExtensions){
			maximal = true;
			for(Set<String> e2: completeExtensions)
				if(e1 != e2 && e2.containsAll(e1)){
					maximal = false;
					break;
				}
			if(maximal)
				result.add(e1);			
		}		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */

	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		// just return the first found preferred extension
		Semantics co = Semantics.CO;
		ClaimBasedInheritedReasoner reasoner = new ClaimBasedInheritedReasoner(co);
		Set<Set<String>> completeExtensions = reasoner.getModels(bbase);
		boolean maximal;
		for(Set<String> e1: completeExtensions){
			maximal = true;
			for(Set<String> e2: completeExtensions)
				if(e1 != e2 && e2.containsAll(e1)){
					maximal = false;
					break;
				}
			if(maximal)
				return e1;			
		}		
		// this should not happen
		throw new RuntimeException("Hmm, did not find a maximal set in a finite number of sets. Should not happen.");
	}
}
