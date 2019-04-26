/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.QuadrupleSetSignature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class captures the signature of a specific
 * first-order language. 
 * 
 * @author Matthias Thimm
 */
public class FolSignature extends QuadrupleSetSignature<Constant,Predicate,Functor,Sort> {
	
	/**
	 * Creates an empty signature 
	 */
	public FolSignature(){
		super();
	}
	
	/**
	 * Creates an empty signature or an otherwise empty signature with equality.
	 * 
	 * @param containsEquality if true, the equality predicate is added to the signature
	 */
	public FolSignature(boolean containsEquality){
		this();
		if (containsEquality) {
			this.add(new EqualityPredicate());
			this.add(new InequalityPredicate()); }
	}
	
	/**
	 * Returns the constants of this fol signature.
	 * @return set of constants
	 */
	public Set<Constant> getConstants() {
		return this.firstSet;
	}
	
	/**
	 * Returns the predicates of this fol signature.
	 * @return set of predicates
	 */
	public Set<Predicate> getPredicates() {
		return this.secondSet;
	}
	
	/**
	 * Returns the functors of this fol signature.
	 * @return set of functors
	 */
	public Set<Functor> getFunctors() {
		return this.thirdSet;
	}
	
	/**
	 * Returns the sorts of this fol signature.
	 * @return set of sorts
	 */
	public Set<Sort> getSorts(){
		return this.fourthSet;
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
	
	/**
	 * Creates a signature with the given objects (should be sorts, constants,
	 * predicates, functors, or formulas) and, if desired, equality.
	 * @param c a collection of items to be added.
	 * @param containsEquality if true, the equality predicate is added to the signature
	 * @throws IllegalArgumentException if at least one of the given objects is
	 * 	 neither a constant, a sort, a predicate, a functor, or a formula.
	 */
	public FolSignature(Collection<?> c, boolean containsEquality) throws IllegalArgumentException{
		this();
		this.addAll(c);
		if (containsEquality) {
			this.add(new EqualityPredicate());
			this.add(new InequalityPredicate()); 
			}
	}
	
	/**
	 * Adds the given object, either a constant, a sort, a predicate, or a functor,
	 * to this signature. If a constant is added its sort is added as well, the same is
	 * for any sort mentioned in predicates and functors. Alternatively, you can
	 * pass over a formula, then all predicates, sorts, constants, and functors
	 * of this formula are added to the signature.
	 * @param obj the object to be added, either a constant, a sort, a predicate, a functor,
	 *    or a formula.
	 * @return 
	 * @throws IllegalArgumentException if the given object is neither a constant, a sort, a
	 *    predicate, a functor, or a formula.
	 */
	@Override
	public void add(Object obj) throws IllegalArgumentException{
		if(obj instanceof Constant){
			fourthSet.add(((Constant)obj).getSort());
			firstSet.add((Constant)obj);
			return;
		}
		if(obj instanceof Sort){
			fourthSet.add((Sort)obj);
			return;
		}
		if(obj instanceof Predicate){
			this.addAll(((Predicate)obj).getArgumentTypes());
			secondSet.add((Predicate)obj);
			return;
		}
		if(obj instanceof Functor){
			this.addAll(((Functor)obj).getArgumentTypes());
			thirdSet.add((Functor)obj);
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
		if(!this.firstSet.containsAll(folFormula.getTerms(Constant.class))) return false;
		if(!this.secondSet.containsAll(folFormula.getPredicates())) return false;
		if(!this.thirdSet.containsAll(folFormula.getFunctors())) return false;
		return true;
	}
	
	
	public Constant getConstant(String s){
		for(Term<?> t: this.firstSet)
			if(((Constant) t).get().equals(s))
				return (Constant) t;
		return null;
	}
	
	public Predicate getPredicate(String s){
		for(Predicate p: this.secondSet)
			if(p.getName().equals(s)) {
				if (s.equals("=="))
					return new EqualityPredicate();
				else if (s.equals("/=="))
					return new InequalityPredicate();
				return p;
			}
		return null;
	}
	
	public Functor getFunctor(String s){
		for(Functor f: this.thirdSet)
			if(f.getName().equals(s))
				return f;
		return null;
	}
	
	public Sort getSort(String s){
		for(Sort st: this.fourthSet)
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
	
	/**
	 * Returns a string representation of the first-order logic signature.
	 * 
	 * @return a string consisting of the sorts with their constants 
	 * followed by the predicates and functors of the signature.
	 */
	public String toString() {
		String result = "[";
		java.util.Iterator<Sort> it = this.fourthSet.iterator();
		while (it.hasNext()) {
			Sort s = it.next();
			Set<Term<?>> containedConstants = new HashSet<Term<?>>();
			for (Term<?> c : s.getTerms())
				if (this.containsConstant(c.toString()))
					containedConstants.add(c); //Print only those constants of the sort that are part of the signature's constants 
			String constants = containedConstants.toString();
			constants = constants.substring(1, constants.length()-1);
			result += s.getName() + " = {" + constants + "}";
			if (it.hasNext()) result+= ", ";
		}
		result += "]";
		
		return result + ", " + this.secondSet.toString() + ", "  + this.thirdSet.toString() ;
	}

	@Override
	public void remove(Object obj) {
		if(obj instanceof Constant){
			fourthSet.remove(((Constant)obj).getSort());
			firstSet.remove((Constant)obj);
			return;
		}
		if(obj instanceof Sort){
			fourthSet.remove((Sort)obj);
			return;
		}
		if(obj instanceof Predicate){
			this.removeAll(((Predicate)obj).getArgumentTypes());
			secondSet.remove((Predicate)obj);
			return;
		}
		if(obj instanceof Functor){
			this.removeAll(((Functor)obj).getArgumentTypes());
			thirdSet.remove((Functor)obj);
			return;
		}
		if(obj instanceof FolFormula){
			this.removeAll(((FolFormula)obj).getTerms(Constant.class));
			this.removeAll(((FolFormula)obj).getPredicates());
			this.removeAll(((FolFormula)obj).getFunctors());
			return;
		}
		throw new IllegalArgumentException("Class " + obj.getClass() + " of parameter is unsupported.");
	
	}
	
}
