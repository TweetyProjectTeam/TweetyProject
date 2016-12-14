package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *	An inference rule from an ABA theory
 * @param <T>	is the type of the language that the ABA theory's rules range over 
 */
public class InferenceRule <T extends Formula> implements ABARule<T> {
	
	/**
	 * The rule's conclusion
	 */
	T head;
	/**
	 * The rule's prerequisites
	 */
	Collection<T> body = new HashSet<T>();


	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return body.size() == 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint()  {
		throw new RuntimeException("arg.aba.InferenceRule.isConstraint not implemented");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#setConclusion(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void setConclusion(T conclusion) {
		head = conclusion;
		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremise(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void addPremise(T premise) {
		body.add(premise);
		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		body.addAll(premises);
		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = head.getSignature();
		for (T t : body)
			sig.addSignature(t.getSignature());
		return sig;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return body;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return head;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.syntax.ABARule#isAssumption()
	 */
	@Override
	public boolean isAssumption() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
