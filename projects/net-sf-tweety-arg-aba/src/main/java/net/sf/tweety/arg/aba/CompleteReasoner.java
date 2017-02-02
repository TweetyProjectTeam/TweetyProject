package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * This reasoner for ABA theories performs inference on the complete extensions.
 * @param <T>	the language of the underlying ABA theory
 */
public class CompleteReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	/**
	 * Creates a new complete reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public CompleteReasoner(BeliefBase beliefBase, int inferenceType) {
		super(beliefBase, inferenceType);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.GeneralABAReasoner#computeExtensions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions() {
		ABATheory<T> abat = (ABATheory<T>)getKnowledgeBase();
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllAdmissbleExtensions();
		l:for(Collection<Assumption<T>> ext : exts) {
			//System.out.println(ext);
			for(Assumption<T> a: abat.getAssumptions()) {
				if(!ext.contains(a)&&abat.defends(ext, a)){
					//System.out.println(a);
					continue l;
				}
			}
			result.add(new HashSet<>(ext));
		}
		return result;
	}

}