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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;


import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ClaimSet;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;

/**
 * Ancestor class for all claim based reasoner.
 * 
 * @author Sebastian Franke
 *
 */
public abstract class AbstractClaimBasedReasoner implements QualitativeReasoner<ClaimBasedTheory,ClaimArgument>, ModelProvider<ClaimArgument,ClaimBasedTheory,ClaimSet>{
	/** Default constructor */
	public AbstractClaimBasedReasoner() { super(); }
	/**get all claim sets that fulfill the given semantics*/
	public abstract Set<ClaimSet> getModels(ClaimBasedTheory bbase);
	/**get one claim sets that fulfill the given semantics*/
	public abstract ClaimSet getModel(ClaimBasedTheory bbase);
	@Override
	public Boolean query(ClaimBasedTheory beliefbase, ClaimArgument formula) {
		// TODO Auto-generated method stub
		return null;
	}
}
