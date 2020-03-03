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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.reasoner.SimplePreferredReasoner;
import net.sf.tweety.arg.dung.reasoner.SimpleStableReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * @author Timothy Gillespie
 *
 */
public class UnstableCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		//Filtering method: Checking cases with lower inconsistency measures first and if they are not applicable go down 
		
		//Check if there is already a stable extension; if yes then then the inconsistency is measured as 0
		Collection<Extension> stableExtensions = new SimpleStableReasoner().getModels(argumentationFramework);
		if(!stableExtensions.isEmpty()) {
			return 0d;
		}
		
		int numberOfNodes = argumentationFramework.getNumberOfNodes();
		
		//Check if there are preferred extensions; if yes calculate from there
		Collection<Extension> preferredExtensions = new SimplePreferredReasoner().getModels(argumentationFramework);
		if(!preferredExtensions.isEmpty()) {
			int max = 0;
			for(Extension singleExtension : preferredExtensions) {
				Set<Argument> extensionAndAttackees = new HashSet<Argument>();
				extensionAndAttackees.addAll(singleExtension);
				
				for(Argument singleArgument : singleExtension)
					extensionAndAttackees.addAll(argumentationFramework.getAttacked(singleArgument));
				
				int size = extensionAndAttackees.size();
				if(max < size)
					max = size;
			}
			
			return (Double)((double) numberOfNodes - max);
			
		}
		
		//If a grounded extension exists then a preferred extension must exist as well
		//If a complete extension exists then a preferred extension must exist as well
		
		//Part missing: How many nodes must be removed to obtain a stable extension if there is no complete extension?
		
		//Removing all but one node is a stable extension except if all nodes have a self loop
		for(Argument singleArgument : argumentationFramework)
			if(argumentationFramework.getEdge(singleArgument, singleArgument) == null)
				 return (Double)((double) numberOfNodes - 1);
		
		//It is always possible to receive a stable extension by removing all arguments which will be the empty set in an empty argumentation framework
		return (Double)((double) numberOfNodes);
	}

	
}
