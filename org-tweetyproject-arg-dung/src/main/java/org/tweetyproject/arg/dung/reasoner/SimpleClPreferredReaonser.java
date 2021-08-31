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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;


import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
/**
 * calculates claim based preferred reasoner
 * @author Sebastian Franke
 *
 */
public class SimpleClPreferredReaonser  extends AbstractClaimBasedReasoner{

	/**
	 * 
	 * @param bbase the claim based thory
	 * @return all extensions of the semantics
	 */
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {
		Semantics co = Semantics.CO;
		SimpleClInheritedReasoner reasoner = new SimpleClInheritedReasoner(co);
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


	/**
	 * 
	 * @param bbase the claim based thory
	 * @return an extensions of the semantics
	 */
	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		// just return the first found preferred extension
		Semantics co = Semantics.CO;
		SimpleClInheritedReasoner reasoner = new SimpleClInheritedReasoner(co);
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
