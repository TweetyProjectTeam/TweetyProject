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
package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * This class represents an argument in ldo. 
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class LdoArgument extends LdoFormula implements Atom, Comparable<LdoArgument> {
	
	/**
	 * The name of the proposition
	 */
	private PropositionalPredicate predicate;

	/** Default-Ctor for dynamic instantiation */
	public LdoArgument() {}
	
	/**
	 * Creates a new proposition of the given name.
	 * @param name the name of the proposition.
	 */
	public LdoArgument(String name){
		this.predicate = new PropositionalPredicate(name);
	}
	
	public LdoArgument(LdoArgument other) {
		this.predicate = new PropositionalPredicate(other.getName());
	}
	
	/**
	 * Returns an argument representation (in Dung-style)
	 * of this LdoArgument.
	 * @return an argument representation (in Dung-style)
	 * of this LdoArgument.
	 */
	public Argument getArgument(){
		return new Argument(this.getName());
	}
	
	/**
	 * @return the name of this proposition.
	 */
	@Override
	public String getName(){
		return this.predicate != null ? this.predicate.getName() : "";
	}
	
	@Override
	public PropositionalPredicate getPredicate() {
		return this.predicate;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		Set<PropositionalPredicate> reval = new HashSet<PropositionalPredicate>();
		reval.add(predicate);
		return reval;
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	
	@Override
	public DungSignature getSignature() {
		DungSignature reval = new DungSignature();
		reval.add(new Argument(this.predicate.getName()));
		return reval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
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
		LdoArgument other = (LdoArgument) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}
	
	@Override
	public LdoArgument clone() {
		return new LdoArgument(this);
	}

	@Override
	public void addArgument(Term<?> arg) {
		throw new UnsupportedOperationException("addArgument not supported by LDO");
	}

	@Override
	public List<? extends Term<?>> getArguments() {
		return new ArrayList<Term<?>>();
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public Set<LdoArgument> getAtoms() {
		Set<LdoArgument> reval = new HashSet<LdoArgument>();
		reval.add(this);
		return reval;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<LdoFormula> getLiterals(){
		Set<LdoFormula> result = new HashSet<LdoFormula>();
		result.add(this);
		return result;
	}
	
	@Override
	public RETURN_SET_PREDICATE setPredicate(Predicate predicate) {
		Predicate old = this.predicate;
		this.predicate = (PropositionalPredicate)predicate;
		return AtomImpl.implSetPredicate(old, this.predicate, new LinkedList<Term<?>>());
	}
	
	@Override
	public int compareTo(LdoArgument o) {
		return predicate.compareTo(o.predicate);
	}
}

