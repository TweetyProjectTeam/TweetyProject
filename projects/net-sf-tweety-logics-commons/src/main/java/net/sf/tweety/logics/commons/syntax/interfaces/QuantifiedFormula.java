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
package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * Interface for a QuantifiedFormula with a set of quantified variables
 * implementing an all- or exist-quantor for example. 
 * @author Tim Janus
 */
public interface QuantifiedFormula extends ComplexLogicalFormula {
	
	/** @return the formula which is quantified */
	SimpleLogicalFormula getFormula();
	
	/** @return a set containing all quantified variables */
	Set<Variable> getQuantifierVariables();
	
	/** @return a set of of unbound variables */
	Set<Variable> getUnboundVariables();
	
	/**
	 * Checks whether this formula contains any quantification.
	 * @return "true" if this formula contains a quantification.
	 */
	boolean containsQuantifier();
	
	/**
	 * Checks whether this formula is well-bound, i.e. whether no variable
	 * bound by a quantifier is again bound by another quantifier within the
	 * first quantifier's range.
	 * @return "true" if this formula is well-bound, "false" otherwise.
	 */
	boolean isWellBound();
	
	
	/**
	 * Checks whether this formula is well-bound, i.e. whether no variable
	 * bound by a quantifier is again bound by another quantifier within the
	 * first quantifier range. Every variable in "boundVariables" is assumed
	 * to be bound already.
	 * @param boundVariables the variables assumed to be bound.
	 * @return "true" if this formula is well-bound, "false" otherwise.
	 */
	boolean isWellBound(Set<Variable> boundVariables);
	
	/**
	 * Checks whether this formula is closed, i.e. whether every variables
	 * occurring in the formula is bound by a quantifier. 
	 * @return "true" if this formula is closed, "false" otherwise.
	 */
	boolean isClosed();
	
	/**
	 * Checks whether this formula is closed, i.e. whether every variables
	 * occurring in the formula is bound by a quantifier. Every variable in
	 * "boundVariables" is already assumed to be bound.
	 * @param boundVariables the variables assumed to be bound.
	 * @return "true" if this formula is closed wrt. "boundVariables", "false" otherwise.
	 */
	boolean isClosed(Set<Variable> boundVariables);
}
