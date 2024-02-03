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
package org.tweetyproject.arg.dung.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * *description missing*
 * @author Timothy Gillespie
 * @param <T> the type of Dung theories used
 */
public class NonGroundedCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
	 Collection<Extension<DungTheory>> groundedExtensions = new SimpleGroundedReasoner().getModels(argumentationFramework);
	 Set<Argument> groundedArguments = new HashSet<Argument>();
	 
	 for(Extension<DungTheory> singleExtension : groundedExtensions)
		 for(Argument singleArgument : singleExtension)
			 groundedArguments.add(singleArgument);
	 
	 Set<Argument> attackees = new HashSet<Argument>();
	 for(Argument singleArgument : groundedArguments)
		 attackees.addAll(argumentationFramework.getAttacked(singleArgument));
	 
	 // Union of the both above
	 Set<Argument> nonGroundedArguments = new HashSet<Argument>();
	 
	 // All arguments of the argumentation framework without grounded arguments and attackees of grounded arguments
	 for(Argument singleArgument : argumentationFramework)
		 if(!groundedArguments.contains(singleArgument) && !attackees.contains(singleArgument))
			 nonGroundedArguments.add(singleArgument);
	 
	 Double nonGroundedCount = (Double) ((double) nonGroundedArguments.size());
	 
	 return nonGroundedCount;
	 
	}

}
