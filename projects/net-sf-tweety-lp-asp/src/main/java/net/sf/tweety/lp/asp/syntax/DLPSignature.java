package net.sf.tweety.lp.asp.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

public class DLPSignature extends Signature {
	
	private Set<DLPPredicate> predicates;
	
	private Set<Constant> constants;
	
	public DLPSignature() {
		predicates = new HashSet<DLPPredicate>();
		constants = new HashSet<Constant>();
	}
	
	public void add(Object obj) {
		if(obj == null)	return;
		if(obj instanceof DLPNeg) {
			obj = ((DLPNeg)obj).getAtom();
		}
		
		if(obj instanceof DLPAtom) {
			DLPAtom a = (DLPAtom)obj;
			predicates.add(a.getPredicate());
			
			for(Term<?> t : a.getTerms()) {
				if(t instanceof Constant) {
					constants.add((Constant)t);
				}
			}
		}
	}

	@Override
	public boolean isSubSignature(Signature other) {
		if(other instanceof DLPSignature) {
			
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.Signature#isOverlappingSignature(net.sf.tweety.Signature)
	 */
	public boolean isOverlappingSignature(Signature other){
		throw new RuntimeException("Implement Me!");
	}
	
	@Override
	public void addSignature(Signature other) {
		if(other instanceof DLPSignature) {
			
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constants == null) ? 0 : constants.hashCode());
		result = prime * result
				+ ((predicates == null) ? 0 : predicates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DLPSignature other = (DLPSignature) obj;
		if (constants == null) {
			if (other.constants != null)
				return false;
		} else if (!constants.equals(other.constants))
			return false;
		if (predicates == null) {
			if (other.predicates != null)
				return false;
		} else if (!predicates.equals(other.predicates))
			return false;
		return true;
	}
}
