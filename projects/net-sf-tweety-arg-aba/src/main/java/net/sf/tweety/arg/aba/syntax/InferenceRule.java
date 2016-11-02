package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class InferenceRule <T extends Invertable> implements ABARule<T> {
	
	T head;
	Collection<T> body = new HashSet<T>();

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
		Signature sig = head.getSignature();
		for (T t : body)
			sig.addSignature(t.getSignature());
		return sig;
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
		String result = "(" + head + " <- ";
		if(body.isEmpty())
			return result+"true)";
		Iterator<T> i = body.iterator();
		result += i.next();
		while (i.hasNext())
			result += ", " + i.next();
		return result + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InferenceRule other = (InferenceRule) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		return true;
	}
	
	

}
