package org.tweetyproject.arg.dung.syntax;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.commons.BeliefBase;

public interface ArgumentationFramework extends BeliefBase{
	
	boolean containsAll(Collection<?> c);
	boolean isAttacked(Argument a, Extension ext);
	Collection<Argument> getNodes();


}
