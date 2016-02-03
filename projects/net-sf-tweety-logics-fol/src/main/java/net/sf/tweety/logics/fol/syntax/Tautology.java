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

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * A tautological formula.
 * @author Matthias Thimm
 */
public class Tautology extends SpecialFormula {

	/**
	 * Creates a new tautology.
	 */
	public Tautology() {		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.TAUTOLOGY();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#hashCode()
	 */
	@Override
	public int hashCode(){
		return 5;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;		
		if (obj == null || getClass() != obj.getClass())
			return false;		
		return true;
	}

	@Override
	public Tautology clone() {
		return new Tautology();
	}
}
