package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class InferenceRule <T extends Invertable> implements ABARule<T> {
	
	T head;
	Collection<T> body = new LinkedList<T>();

	public InferenceRule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isFact() {
		return body.size() == 0;
	}

	@Override
	public boolean isConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConclusion(T conclusion) {
		head = conclusion;
		
	}

	@Override
	public void addPremise(T premise) {
		body.add(premise);
		
	}

	@Override
	public void addPremises(Collection<? extends T> premises) {
		body.addAll(premises);
		
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends T> getPremise() {
		return body;
	}

	@Override
	public T getConclusion() {
		return head;
	}

	@Override
	public boolean isAssumption() {
		return false;
	}

	@Override
	public String toString() {
		String result = head + " <- ";
		if(body.isEmpty())
			return result+"true";
		Iterator<T> i = body.iterator();
		result += i.next();
		while (i.hasNext())
			result += ", " + i.next();
		return result;
	}
	
	

}
