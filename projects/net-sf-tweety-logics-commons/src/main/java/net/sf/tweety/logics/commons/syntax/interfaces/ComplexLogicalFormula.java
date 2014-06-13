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
package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.Map;

/**
 * A complex logical formula can contain arguments referred as terms, this
 * interface provides methods to substitute and exchange those terms. It also 
 * provides utility functions like isGround() and isWellFormed().
 * 
 * @author Tim Janus
 * @author Matthias Thimm
 */
public interface ComplexLogicalFormula extends SimpleLogicalFormula, LogicStructure {
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and returns the new formula.
	 * @param v the term to be substituted.
	 * @param t the term to substitute.
	 * @return a formula where every occurrence of "v" is replaced
	 * 		by "t".
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 */
	ComplexLogicalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * Substitutes all occurrences of all terms "v" in map.keyset() in this formula
	 * by map.get(v) and returns the new formula.<br>
	 * @param map a mapping defining which terms to be substituted.
	 * @return a formula where every term in map.keyset() has been replaced by map.get(v).
	 * @throws IllegalArgumentException if any term and its mapping are of different sorts
	 */
	ComplexLogicalFormula substitute(Map<? extends Term<?>,? extends Term<?>> map) 
			throws IllegalArgumentException;
	
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and at the same time replaces all occurrences of term "t"
	 * by term "v" and eventually returns the new formula.
	 * @param v a term.
	 * @param t a term.
	 * @return a new logical formula with both "v" and "t" exchanged.
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 */
	ComplexLogicalFormula exchange(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * Checks whether this formula is ground, i.e. whether there appears
	 * no variable in this formula.
	 * @return "true" if this formula is ground.
	 */
	boolean isGround();
	
	/**
	 * Checks if this formula is well formed in the logical langauge. What well-
	 * formed means is highly language dependent and the documentation of implementing 
	 * sub classes shall describe the well formed term for the language they model.
	 * @return true if the formula is well-formed, false otherwise
	 */
	boolean isWellFormed();
	
	@Override
	ComplexLogicalFormula clone();
}
