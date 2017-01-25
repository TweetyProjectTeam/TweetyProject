/**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 */
public class PreferredReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	public PreferredReasoner(BeliefBase beliefBase, int inferenceType) {
		super(beliefBase, inferenceType);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions() {
		ABATheory<T> abat = (ABATheory<T>)getKnowledgeBase();
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllAdmissbleExtensions();
		l:for(Collection<Assumption<T>> ext : exts) {
			for(Collection<Assumption<T>> ext2 : exts) {
				if (ext2!=ext&&ext2.containsAll(ext))
					continue l;
			}
			result.add(new HashSet<>(ext));
		}
		return result;
	}
}
