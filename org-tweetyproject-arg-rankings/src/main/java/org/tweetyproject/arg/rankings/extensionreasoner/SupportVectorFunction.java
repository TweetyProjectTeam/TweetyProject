package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.Vector;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * Interface concerning the function that computes the support vector in the <code>OrderBasedExtensionReasoner<code>
 * 
 * @author Benjamin Birner
 *
 */
public interface SupportVectorFunction {

	
	/**
	 * computes the support vector of <code>ext<code>
	 * 
	 * @param ext extension to compute its support vector
	 * @param extensions set of extensions to which the computation is referred to.
	 * @param aggregate indicates if the support vector shall be aggregated
	 * @return support vector of <code>ext<code>
	 */
	Vector<Double> getSupportVector(Extension<DungTheory> ext, Collection<Extension<DungTheory>> extensions, boolean aggregate);
	
}
