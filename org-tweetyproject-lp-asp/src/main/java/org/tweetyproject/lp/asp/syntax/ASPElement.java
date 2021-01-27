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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.Map;
import java.util.Set;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class acts as an abstract base class for elements
 * of ASP rules.
 * 
 * @author Anna Gessler
 *
 */
public abstract class ASPElement implements ComplexLogicalFormula {
	@Override
	public abstract Set<Predicate> getPredicates();
	
	@Override
	public abstract ASPElement substitute(Term<?> t, Term<?> v);
	
	@Override
	public abstract FolSignature getSignature();
	
	@Override
	public abstract Set<ASPAtom> getAtoms();
	
	@Override
	public abstract ASPElement clone();
	
	public ASPElement substitute(Map<? extends Term<?>, ? extends Term<?>> map) throws IllegalArgumentException {
		ASPElement f = this;
		for(Term<?> v: map.keySet())
			f = f.substitute(v,map.get(v));
		return f;
	}

	public ASPElement exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		if(!v.getSort().equals(t.getSort()))
			throw new IllegalArgumentException("Terms '" + v + "' and '" + t + "' are of different sorts.");
		Constant temp = new Constant("$TEMP$", v.getSort());
		ASPElement rf = this.substitute(v, temp);
		rf = rf.substitute(t, v);
		rf = rf.substitute(temp, t);
		// remove temporary constant from signature
		v.getSort().remove(temp);	
		return rf;
	}

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}

	@Override
	public boolean isGround() {
		return this.getTerms(Variable.class).isEmpty();
	}

	@Override
	public boolean isWellFormed() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}
	
	/**
	 * Returns a representation of this ASP element in clingo (potassco) syntax.
	 * See https://potassco.org/ for more information.
	 * 
	 * @return String representation in clingo syntax
	 */
	public String printToClingo() {
		return this.toString();
	}

}
