package org.tweetyproject.arg.dung.reasoner;


import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ClaimSet;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;


public abstract class AbstractClaimBasedReasoner implements QualitativeReasoner<ClaimBasedTheory,ClaimArgument>, ModelProvider<ClaimArgument,ClaimBasedTheory,ClaimSet>{
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
