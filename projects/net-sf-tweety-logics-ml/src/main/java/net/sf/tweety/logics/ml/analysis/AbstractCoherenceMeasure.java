package net.sf.tweety.logics.ml.analysis;

import java.io.Serializable;

import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.MarkovLogicNetwork;

/**
 * This class represents an abstract coherence measure, i.e. a function
 * that measures the coherence of an MLN by comparing the probabilities for 
 * the MLN's formulas with the intended ones.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractCoherenceMeasure implements Serializable{

	private static final long serialVersionUID = 8888349459869328287L;

	/** Measures the coherence of the given MLN using the given reasoner.
	 * @param mln some MLN
	 * @param reasoner some reasoner
	 * @param signature a signature
	 * @return the coherence measure of the MLN.
	 */
	public abstract double coherence(MarkovLogicNetwork mln, Reasoner reasoner, FolSignature signature);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
}
