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

import java.util.*;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This abstract class captures the common functionalities of both
 * formulas and terms.
 * @author Matthias Thimm
 */
public abstract class LogicStructure {
	/**
	 * Returns all constants that appear in this structure.
	 * @return all constants that appear in this structure.
	 */
	public abstract Set<Constant> getConstants();
	
	/**
	 * Returns all functors that appear in this structure.
	 * @return all functors that appear in this structure.
	 */
	public abstract Set<Functor> getFunctors();
	
	/**
	 * Returns all variables that appear in this structure.
	 * @return all variables that appear in this structure.
	 */
	public abstract Set<Variable> getVariables();
	
	/**
	 * Returns all functional terms that appear in this structure.
	 * @return all functional terms that appear in this structure.
	 */
	public abstract Set<FunctionalTerm> getFunctionalTerms();
	
	/**
	 * Checks whether this structure contains any functional terms.
	 * @return "true" if this structure contains a functional term.
	 */
	public boolean containsFunctionalTerms(){
		return !this.getFunctionalTerms().isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public abstract String toString();
	
}
