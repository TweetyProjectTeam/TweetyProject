package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class ABATheory<T extends Invertable> implements BeliefBase {

	private Collection<InferenceRule<T>> rules = new HashSet<>();
	private Collection<Assumption<T>> assumptions = new HashSet<>();

	public ABATheory() {
		// TODO Auto-generated constructor stub
	}

	public void add(ABARule<T> rule) {
		if (rule instanceof Assumption)
			assumptions.add((Assumption<T>) rule);
		else if (rule instanceof InferenceRule)
			rules.add((InferenceRule<T>) rule);
	}

	public Collection<InferenceRule<T>> getRules() {
		return rules;
	}

	public Collection<Assumption<T>> getAssumptions() {
		return assumptions;
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	public DungTheory asDungTheory() {
		return null;
	}

}
