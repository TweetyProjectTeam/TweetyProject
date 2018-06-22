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
 *  Copyright 2017 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.deductive.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Basic data structure for handling simple rule
 * 
 * @author Federico Cerutti <federico.cerutti@acm.org>
 *
 */
public class SimplePlRule implements Rule<PropositionalFormula, PropositionalFormula> {
	
	private PropositionalFormula claim = null;
	private Set<PropositionalFormula> support = null;

	public SimplePlRule(){
		support = new HashSet<PropositionalFormula>();
	}
	
	public SimplePlRule(PropositionalFormula _claim){
		claim = _claim;
		support = new HashSet<PropositionalFormula>();
	}
	
	public SimplePlRule(PropositionalFormula _claim, Set<PropositionalFormula> _support){
		claim = _claim;
		support = _support;
	}

	public void addPremise(PropositionalFormula arg0) {
		this.support.add(arg0);
		
	}

	public void addPremises(Collection<? extends PropositionalFormula> arg0) {
		this.support.addAll(arg0);
		
	}

	public PropositionalFormula getConclusion() {
		return this.claim;
	}

	public Collection<? extends PropositionalFormula> getPremise() {
		return this.support;
	}

	public Signature getSignature() {
		PropositionalSignature sig = new PropositionalSignature();
		sig.add(this.claim.getSignature());
		for(Formula p: this.support)
			sig.add((PropositionalSignature) p.getSignature());
		return sig;
	}

	public boolean isConstraint() {
		return false;
	}

	public boolean isFact() {
		return this.support.isEmpty();
	}

	public void setConclusion(PropositionalFormula arg0) {
		this.claim = arg0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePlRule other = (SimplePlRule) obj;
		if (!this.claim.equals(other.claim))
			return false;
		if (this.support == null){
			if (other.support != null){
				return false;
			}
		} else if (!support.equals(other.support))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((support == null) ? 0 : support.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		if (this.isFact())
			return this.claim.toString();
		return this.support.toString() + " -> " + this.claim.toString() ;
	}


}
