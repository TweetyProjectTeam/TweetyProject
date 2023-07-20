package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;




/**
 * Interface concerning the comparison criteria of comparing two Extensions
 * This Interface is used in the <code>getModels()<code> method in the <code>CopelandBasedComparisonExtensionReasoner<code> class.
 * 
 * @author Benjamin Birner
 *
 */
public interface ComparisonCriteria {
	
	
	/**
	 * Compares the Extensions <code>e1<code> and <code>e2<code> and selects the best one. 
	 * This Method is used in the <code>getModels()<code> method in the <code>CopelandBasedComparisonExtensionReasoner<code> class.
	 * e1 < e2 means that e2 is the better Extension and e1 > e2 means that e1 is the better one.
	 * 
	 * @param dung an argumentation framework
	 * @param e1 an Extension
	 * @param e2 an Extension
	 * @return value 0 if e1 == e2; value less then 0 if e1 < e2 and value greater than 0 if e1 > e2
	 * @throws IllegalAccessException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 */
	int compare(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

}
