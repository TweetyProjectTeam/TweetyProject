package net.sf.tweety.logics.ml.analysis;

import java.util.List;

import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.MarkovLogicNetwork;

/**
 * This interface represents a compatibility measure for MLNs.
 * Given a set of MLNs it returns a value indicating how compatible
 * those MLNs are (i.e. how much the probabilities change when merging
 * the MLNs).
 * 
 * @author Matthias Thimm
 */
public interface CompatibilityMeasure {

	/**
	 * Measures the compatibility of the given MLNs wrt. the given signatures using the
	 * given reasoner.
	 * @param mlns a list of MLNs.
	 * @param reasoner some reasoner.
	 * @param signatures a set of signatures, one for each MLN.
	 */
	public abstract double compatibility(List<MarkovLogicNetwork> mlns, Reasoner reasoner, List<FolSignature> signatures);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
}
