package org.tweetyproject.arg.dung.reasoner;


import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;


public abstract class AbstractClaimBasedReasoner {
	/**get all claim sets that fulfill the given semantics*/
	public abstract Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase);
	/**get one claim sets that fulfill the given semantics*/
	public abstract Set<String> getModel(ArgumentationFramework<Argument> bbase);
}
