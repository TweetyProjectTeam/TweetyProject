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
package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * this interface models a logical literal used in
 * extended logic programs. 
 * 
 * @author Thomas Vengels
 *
 */
public interface ELPLiteral {
	
	/**
	 * 
	 * @return	true if this literal is default negated
	 */
	public boolean	isDefaultNegated();

	/**
	 * 
	 * @return	true if this literal is strictly negated
	 */
	public boolean	isStrictNegated();
		
	/**
	 * 
	 * @return	true if this literal is an atom
	 */
	public boolean isAtom();
	
	
	/**
	 * this method indicates if the literal appearing in
	 * a program is a build-in or external dlv predicate
	 * 
	 * @return true if this object models a dlv predicate
	 */
	public boolean isPredicate();
	
	
	/**
	 * returns a possibly nested literal, or the object itself 
	 * if this literal is not (strict or default) negated.
	 * 
	 * @return	nested literal
	 */
	public ELPLiteral getLiteral();
	
	/**
	 * returns the atom of this literal
	 * 
	 * @return	atom
	 */
	public ELPAtom getAtom();
	
}
