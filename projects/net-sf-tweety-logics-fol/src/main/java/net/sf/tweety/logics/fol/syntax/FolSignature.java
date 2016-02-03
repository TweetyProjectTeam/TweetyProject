/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class captures the signature of a specific
 * first-order language.
 * @author Matthias Thimm
 */
public class FolSignature extends Signature{
	
	private Set<Constant> constants;
	private Set<Sort> sorts;
	private Set<Predicate> predicates;
	private Set<Functor> functors;
	
	/**
	 * Creates an empty signature 
	 */
	public FolSignature(){
		this.constants = new HashSet<Constant>();
		this.sorts = new HashSet<Sort>();
		this.predicates = new HashSet<Predicate>();
		this.functors = new HashSet<Functor>();
	}
	
	/**
	 * Creates a signature with the given objects (should be sorts, constants,
	 * predicates, functors, or formulas).
	 * @param c a collection of items to be added.
	 * @throws IllegalArgumentException if at least one of the given objects is
	 * 	 neither a constant, a sort, a predicate, a functor, or a formula.
	 */
	public FolSignature(Collection<?> c) throws IllegalArgumentException{
		this();
		this.addAll(c);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Signature#isSubSignature(net.sf.tweety.kr.Signature)
	 */
	@Override
	public boolean isSubSignature(Signature other){
		if(!(other instanceof FolSignature))
			return false;
		FolSignature o = (FolSignature) other;
		if(!o.constants.containsAll(this.constants)) return false;
		if(!o.functors.containsAll(this.functors)) return false;
		if(!o.predicates.containsAll(this.predicates)) return false;
		if(!o.sorts.containsAll(this.sorts)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Signature#isOverlappingSignature(net.sf.tweety.Signature)
	 */
	@Override
	public boolean isOverlappingSignature(Signature other){
		if(!(other instanceof FolSignature))
			return false;
		FolSignature o = (FolSignature) other;
		for(Object obj: o.constants) if(this.constants.contains(obj)) return true;
		for(Object obj: o.functors) if(this.functors.contains(obj)) return true;
		for(Object obj: o.predicates) if(this.predicates.contains(obj)) return true;
		for(Object obj: o.sorts) if(this.sorts.contains(obj)) return true;
		return true;
	}
	
	/**
	 * Adds the given object, either a constant, a sort, a predicate, or a functor,
	 * to this signature. If a constant is added its sort is added as well, the same is
	 * for any sort mentioned in predicates and functors. Alternatively, you can
	 * pass over a formula, then all predicates, sorts, constants, and functors
	 * of this formula are added to the signature.
	 * @param obj the object to be added, either a constant, a sort, a predicate, a functor,
	 *    or a formula.
	 * @throws IllegalArgumentException if the given object is neither a constant, a sort, a
	 *    predicate, a functor, or a formula.
	 */
	public void add(Object obj) throws IllegalArgumentException{
		if(obj instanceof Constant){
			sorts.add(((Constant)obj).getSort());
			constants.add((Constant)obj);
			return;
		}
		if(obj instanceof Sort){
			sorts.add((Sort)obj);
			return;
		}
		if(obj instanceof Predicate){
			predicates.add((Predicate)obj);
			this.addAll(((Predicate)obj).getArgumentTypes());
			return;
		}
		if(obj instanceof Functor){
			functors.add((Functor)obj);
			this.addAll(((Functor)obj).getArgumentTypes());
			return;
		}
		if(obj instanceof FolFormula){
			this.addAll(((FolFormula)obj).getTerms(Constant.class));
			this.addAll(((FolFormula)obj).getPredicates());
			this.addAll(((FolFormula)obj).getFunctors());
			return;
		}
		throw new IllegalArgumentException("Class " + obj.getClass() + " of parameter is unsupported.");
	}
	
	/**
	 * Adds items of the given collection to this signature. These should be either 
	 * constants, sorts, predicates, or functors. Alternatively, you can pass over formulas,
	 * then all predicates, sorts, constants, and functors of this formula are added t
	 * the signature.
	 * @param c the collection of items to be added
	 * @throws IllegalArgumentException if at least one of the given objects is
	 * 	 neither a constant, a sort, a predicate, a functor, or a formula.
	 */
	public void addAll(Collection<?> c) throws IllegalArgumentException{
		for(Object obj: c)
			this.add(obj);
	}
	
	/**
	 * Checks whether the given formula can be represented by this signature.
	 * @param folFormula A formula to be checked.
	 * @return "true" if the given formula is representable, "false" otherwise.
	 */
	public boolean isRepresentable(FolFormula folFormula){
		if(!this.constants.containsAll(folFormula.getTerms(Constant.class))) return false;
		if(!this.predicates.containsAll(folFormula.getPredicates())) return false;
		if(!this.functors.containsAll(folFormula.getFunctors())) return false;
		return true;
	}

	public Set<Constant> getConstants(){
		return this.constants;
	}
	
	public Set<Predicate> getPredicates(){
		return this.predicates;
	}
	
	public Set<Functor> getFunctors(){
		return this.functors;
	}
	
	public Set<Sort> getSorts(){
		return this.sorts;
	}
	
	public Constant getConstant(String s){
		for(Term<?> t: this.constants)
			if(((Constant) t).get().equals(s))
				return (Constant) t;
		return null;
	}
	
	public Predicate getPredicate(String s){
		for(Predicate p: this.predicates)
			if(p.getName().equals(s))
				return p;
		return null;
	}
	
	public Functor getFunctor(String s){
		for(Functor f: this.functors)
			if(f.getName().equals(s))
				return f;
		return null;
	}
	
	public Sort getSort(String s){
		for(Sort st: this.sorts)
			if(st.getName().equals(s))
				return st;
		return null;
	}
	
	public boolean containsConstant(String s){
		return this.getConstant(s) != null;
	}

	public boolean containsPredicate(String s){
		return this.getPredicate(s) != null;
	}
	
	public boolean containsFunctor(String s){
		return this.getFunctor(s) != null;
	}
	
	public boolean containsSort(String s){
		return this.getSort(s) != null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constants == null) ? 0 : constants.hashCode());
		result = prime * result
				+ ((functors == null) ? 0 : functors.hashCode());
		result = prime * result
				+ ((predicates == null) ? 0 : predicates.hashCode());
		result = prime * result + ((sorts == null) ? 0 : sorts.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolSignature other = (FolSignature) obj;
		if (constants == null) {
			if (other.constants != null)
				return false;
		} else if (!constants.equals(other.constants))
			return false;
		if (functors == null) {
			if (other.functors != null)
				return false;
		} else if (!functors.equals(other.functors))
			return false;
		if (predicates == null) {
			if (other.predicates != null)
				return false;
		} else if (!predicates.equals(other.predicates))
			return false;
		if (sorts == null) {
			if (other.sorts != null)
				return false;
		} else if (!sorts.equals(other.sorts))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.Signature#addSignature(net.sf.tweety.Signature)
	 */
	@Override
	public void addSignature(Signature other) {
		if(!(other instanceof FolSignature))
			return;
		FolSignature folSig = (FolSignature) other;
		this.constants.addAll(folSig.constants);
		this.functors.addAll(folSig.functors);
		this.predicates.addAll(folSig.predicates);
		this.sorts.addAll(folSig.sorts);
		
	}
	
}
