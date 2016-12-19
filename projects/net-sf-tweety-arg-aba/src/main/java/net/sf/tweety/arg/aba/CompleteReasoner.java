package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.Formula;

public class CompleteReasoner<T extends Formula> implements ABAReasoner<T> {
	ABATheory<T> abat;
	int inferenceType;

	public CompleteReasoner(ABATheory<T> abat, int inferenceType) {
		super();
		this.abat = abat;
		this.inferenceType = inferenceType;
	}

	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions() {
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllExtensions();
		for(Collection<Assumption<T>> ext : exts) {
			if(!abat.isAdmissible(ext)) 
				continue;
			for(Assumption<T> a: abat.getAssumptions()) {
				if(!ext.contains(a)&&abat.defends(ext, a))
					continue;
			}
			result.add(new HashSet<>(ext));
		}
		return result;
	}

}