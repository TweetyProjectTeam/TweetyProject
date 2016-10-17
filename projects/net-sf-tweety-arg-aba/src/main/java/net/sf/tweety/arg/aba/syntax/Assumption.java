package net.sf.tweety.arg.aba.syntax;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class Assumption <T extends Invertable> implements ABARule< T> {
	T assumption;

	public Assumption(T assumption) {
		super();
		this.assumption = assumption;
	}

	@Override
	public boolean isFact() {
		return true;
	}

	@Override
	public boolean isConstraint() {
		return false;
	}

	@Override
	public void setConclusion(T conclusion) {
		assumption = conclusion;
		
	}

	@Override
	public void addPremise(T premise) {
		throw new RuntimeException("Cannot add Premise to Assumtion");			
	}

	@Override
	public void addPremises(Collection<? extends T> premises) {
		throw new RuntimeException("Cannot add Premise to Assumtion");	
		
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends T> getPremise() {
		return new ArrayList<>();
	}

	@Override
	public T getConclusion() {
		return assumption;
	}

	@Override
	public boolean isAssumption() {
		return true;
	}

	@Override
	public String toString() {
		return assumption.toString();
	}
	
	

}
