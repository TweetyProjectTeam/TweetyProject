package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

public class CompleteReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	public CompleteReasoner(BeliefBase beliefBase, int inferenceType) {
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
			for(Assumption<T> a: abat.getAssumptions()) {
				if(!ext.contains(a)&&abat.defends(ext, a))
					continue l;
			}
			result.add(new HashSet<>(ext));
		}
		return result;
	}

}