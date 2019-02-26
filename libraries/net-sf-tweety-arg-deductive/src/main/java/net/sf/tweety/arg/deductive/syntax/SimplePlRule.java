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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.deductive.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Basic data structure for handling simple rule
 * 
 * @author Federico Cerutti <federico.cerutti@acm.org>
 *
 */
public class SimplePlRule implements Rule<PlFormula, PlFormula> {
	
	private PlFormula claim = null;
	private Set<PlFormula> support = null;

	public SimplePlRule(){
		support = new HashSet<PlFormula>();
	}
	
	public SimplePlRule(PlFormula _claim){
		claim = _claim;
		support = new HashSet<PlFormula>();
	}
	
	public SimplePlRule(PlFormula _claim, Set<PlFormula> _support){
		claim = _claim;
		support = _support;
	}

	public void addPremise(PlFormula arg0) {
		this.support.add(arg0);
		
	}

	public void addPremises(Collection<? extends PlFormula> arg0) {
		this.support.addAll(arg0);
		
	}

	public PlFormula getConclusion() {
		return this.claim;
	}

	public Collection<? extends PlFormula> getPremise() {
		return this.support;
	}

	public Signature getSignature() {
		PlSignature sig = new PlSignature();
		sig.add(this.claim.getSignature());
		for(Formula p: this.support)
			sig.add((PlSignature) p.getSignature());
		return sig;
	}

	public boolean isConstraint() {
		return false;
	}

	public boolean isFact() {
		return this.support.isEmpty();
	}

	public void setConclusion(PlFormula arg0) {
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
