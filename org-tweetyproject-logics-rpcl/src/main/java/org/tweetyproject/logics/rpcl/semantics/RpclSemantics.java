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
package org.tweetyproject.logics.rpcl.semantics;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.rpcl.syntax.*;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.term.*;


/**
 * This interface describes semantics for relational probabilistic logic.
 * 
 * @author Matthias Thimm
 * 
 */
public interface RpclSemantics {

	/**
	 * Checks whether the given probability distribution satisfies the given
	 * conditional wrt. this semantics.
	 * @param p a probability distribution
	 * @param r a relational probability conditional.
	 * @return "true" iff the given distribution satisfies the given conditional.
	 */
	public boolean satisfies(RpclProbabilityDistribution<?> p, RelationalProbabilisticConditional r);
	
	/**
	 * Returns the mathematical statement corresponding to the satisfaction
	 * of the given conditional wrt. this semantics and the given signature.
	 * @param r a relational probabilistic conditional
	 * @param signature a fol signature
	 * @param worlds2vars a map mapping the interpretations of the fol to mathematical variables.
	 * @return the mathematical statement corresponding to the satisfaction
	 * of the given conditional wrt. this semantics and the given signature.
	 */
	public Statement getSatisfactionStatement(RelationalProbabilisticConditional r, FolSignature signature, Map<Interpretation<FolBeliefSet,FolFormula>,FloatVariable> worlds2vars);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();
}
