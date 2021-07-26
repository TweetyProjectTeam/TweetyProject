package org.tweetyproject.arg.dung.syntax;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.commons.BeliefBase;

/**
 * 
 * @author Matthias Thimm
 *
 */
public interface ArgumentationFramework extends BeliefBase{
	/**
	 * 
	 * @param c parameter
	 * @return containsAll
	 */
	boolean containsAll(Collection<?> c);
	/**
	 * 
	 * @param a parameter
	 * @param ext parameter
	 * @return isAttacked
	 */
	boolean isAttacked(Argument a, Extension ext);
	Collection<Argument> getNodes();


}
