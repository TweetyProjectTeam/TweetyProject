package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.Vector;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * Interface for computing a strength vector which is required in the <code>RankBasedPairwiseExtensionReasoner<code> class.
 * 
 * @author Benjamin Birner
 *
 */
public interface StrengthVectorFunction {


	/**
	 * This method computes the strength vector.
	 * 
	 * @param ext Extension of which the strength vector shall be calculated
	 * @param extensions set of extension
	 * @param dung an Argumentation Framework
	 * @return the strength vector of <code>ext<code>
	 */
	Vector<Double> getStrengthVector(Extension<DungTheory> ext, Collection<Extension<DungTheory>> extensions, DungTheory dung);
	
	
}
