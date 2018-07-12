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
package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * Provides common functionalities for all modalities in LDO.
 * @author Matthias Thimm
 *
 */
public abstract class AbstractLdoModality extends LdoFormula{

	/** The inner formula of this modality */
	private LdoFormula innerFormula;
	
	/**
	 * Creates a new modality for the given inner formula
	 * @param innerFormula some ldo formula
	 */
	public AbstractLdoModality(LdoFormula innerFormula){
		this.innerFormula = innerFormula;
	}
	
	@Override
	public Set<LdoArgument> getAtoms() {
		return this.innerFormula.getAtoms();
	}

	/**
	 * Returns the inner formula of this modality.
	 * @return the inner formula of this modality.
	 */
	public LdoFormula getInnerFormula(){
		return this.innerFormula;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return this.innerFormula.getPredicates();
	}

	@Override
	public Set<LdoFormula> getLiterals() {
		return this.innerFormula.getLiterals();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((innerFormula == null) ? 0 : innerFormula.hashCode());
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
		AbstractLdoModality other = (AbstractLdoModality) obj;
		if (innerFormula == null) {
			if (other.innerFormula != null)
				return false;
		} else if (!innerFormula.equals(other.innerFormula))
			return false;
		return true;
	}

	@Override
	public abstract LdoFormula clone();
	
}
