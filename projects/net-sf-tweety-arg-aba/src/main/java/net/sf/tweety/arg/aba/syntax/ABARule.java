package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class ABARule <T extends Invertable> implements Rule<T, T> {

	public ABARule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isFact() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConclusion(T conclusion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPremise(T premise) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPremises(Collection<? extends T> premises) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends T> getPremise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getConclusion() {
		// TODO Auto-generated method stub
		return null;
	}

}
