/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.lp.asp.syntax;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * this class represents an aggregate function. aggregates
 * are functions like sum, times, count over a symbolic set,
 * a set of literals and local variables.
 * 
 * @todo use an enum for relations instead a string
 * @todo implement complement()
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 */
public class Aggregate extends DLPElementAdapter implements DLPElement {

	protected SymbolicSet	symSet = null;
	protected Term<?> leftGuard = null, rightGuard = null;
	protected String leftOp = null, rightOp = null;
	protected String functor;
	
	public Aggregate(Term<?> leftGuard, 
			String leftOp, String functor, SymbolicSet ss) {
		this(leftGuard, leftOp, functor, ss, null, null);
	}
	
	public Aggregate(String functor, SymbolicSet ss,
			String rightOp, Term<?> rightGuard) {
		this(null, null, functor, ss, rightOp, rightGuard);
	}
	
	public Aggregate(Term<?> leftGuard, 
			String leftOp, String functor, SymbolicSet ss,
			String rightOp, Term<?> rightGuard) {
		this.functor = functor;
		this.leftOp = leftOp;
		this.leftGuard = leftGuard;
		this.rightOp = rightOp;
		this.rightGuard = rightGuard;
		this.symSet = ss;
	}
	
	public Aggregate(String functor, SymbolicSet symSet) {
		this.functor = (functor);
		this.symSet = symSet;
	}
	
	public Aggregate(Aggregate other) {
		this.functor = other.functor;
		this.leftGuard = (Term<?>)other.leftGuard.clone();
		this.rightGuard = (Term<?>)other.rightGuard.clone();
		this.symSet = (SymbolicSet)other.symSet.clone();
	}
	
	
	public boolean	hasLeftGuard() {
		return	leftGuard != null;
	}
	
	public boolean hasRightGuard() {
		return	rightGuard != null;
	}
	
	public Term<?>	getLeftGuard() {
		return leftGuard;
	}
	
	public String getLeftOperator() {
		return leftOp;
	}
	
	public Term<?> getRightGuard() {
		return rightGuard;
	}
	
	public String getRightOperator() {
		return rightOp;
	}
	
	public void setLeftGuard(Term<?> guard, String rel) {
		this.leftGuard = guard;
		this.leftOp = rel;
	}
	
	public void setRightGuard(Term<?> guard, String rel) {
		this.rightGuard = guard;
		this.rightOp = rel;
	}
	
	public String getFunctor() {
		return this.functor;
	}
	
	public SymbolicSet getSymbolicSet() {
		return this.symSet;
	}
	
	@Override
	public String toString() {
		String ret = "";
		if (this.leftGuard != null) {
			ret += this.leftGuard + " " + this.leftOp + " ";
		}
		ret += functor + this.symSet;
		if (this.rightGuard != null) {
			ret += " " + this.rightOp + " " + this.rightGuard;
		}
		return ret;
	}

	@Override
	/** @todo implement correctly */
	public SortedSet<DLPLiteral> getLiterals() {
		return new TreeSet<DLPLiteral>();
	}

	@Override
	public boolean isGround() {
		return false;
	}
	
	@Override
	public Aggregate clone() {
		return new Aggregate(this);
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		if(leftGuard != null)
			reval.add(leftGuard);
		if(rightGuard != null)
			reval.add(rightGuard);
		return reval;
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		return new HashSet<DLPAtom>();
	}

	@Override
	public Aggregate substitute(Term<?> t, Term<?> v) {
		Aggregate reval = new Aggregate(this);
		if(t.equals(leftGuard)) {
			reval.leftGuard = v;
		}
		if(t.equals(rightGuard)) {
			reval.rightGuard = v;
		}
		return reval;
	}

	@Override
	public DLPSignature getSignature() {
		DLPSignature reval = new DLPSignature();
		reval.add(leftGuard);
		reval.add(rightGuard);
		return reval;
	}
	
	@Override
	public int hashCode() {
		int sum = symSet.hashCode() + functor.hashCode();
		sum += leftGuard != null ? leftGuard.hashCode() : 0;
		sum += leftOp != null ? leftOp.hashCode() : 0;
		sum += rightGuard != null ? rightGuard.hashCode() : 0;
		sum += rightOp != null ? rightOp.hashCode() : 0;
		return sum * 13;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Aggregate)) {
			return false;
		}
		Aggregate o = (Aggregate)other;
		if(!functor.equals(o.functor) ||
			!symSet.equals(o.symSet)) {
			return false;
		}
		
		return leftGuard == null ? o.leftGuard == null : leftGuard.equals(o.leftGuard) &&
				rightGuard == null ? o.rightGuard == null : rightGuard.equals(o.rightGuard) &&
				leftOp == null ? o.leftOp == null : leftOp.equals(o.leftOp) &&
				rightOp == null ? o.rightOp == null : rightOp.equals(o.rightOp);
	}
}
