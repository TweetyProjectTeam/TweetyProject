package net.sf.tweety.logics.cl.kappa;

import java.util.Set;

/**
 * This interface defines a kappa term, that can be evaluated, it
 * can return its value, which is -1 as long as the kappa-term cannot
 * be evaluated and it can return a value for that we know that the kappa-term
 * value is greatar or equal to that value.
 * 
 * @author Tim Janus
 */
public interface KappaTerm {
	/**
	 * Tries to evaluate the kappa term, if the evaluation is successful then the
	 * next call of value returns the evaluated value.
	 * @return	True if the evaluation is sucessful, false otherwise
	 */
	boolean evaluate();
	
	/**
	 * @return 	The value of this kappa term or -1 if the kappa-term's evaluate() method
	 * 			returns false. 
	 */
	int value();
	
	/**
	 * @return 	The minimum value of this kappa term, such that the caller knows the kappa term is
	 * 			greater or equal the returned value.
	 */
	int greaterEqualThan();
	
	/**
	 * Processes all the sub-terms
	 * @return	A set containing all the sub-terms of the kappa term
	 */
	Set<KappaTerm> getSubTerms();
}
