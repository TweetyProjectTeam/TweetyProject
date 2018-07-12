package net.sf.tweety.commons.postulates;

import net.sf.tweety.commons.Formula;

/**
 * Classes implementing this interface can be evaluated
 * wrt. rationality postulates through the <code>PostulateEvaluator.java</code>
 * class.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas this approach expects.
 */
public interface PostulateEvaluatable<S extends Formula> {

}
