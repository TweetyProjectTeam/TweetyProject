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
package net.sf.tweety.commons;

/**
 * A language for a given signature specifies which formulas, knowledge bases,
 * and queries can be constructed. Its main purpose is to provide the methods
 * "isRepresentable" which test given formulas, queries, and knowledge bases
 * if they are representable in this language. 
 * @author Matthias Thimm
 */
public abstract class Language {
	
	/**
	 * The signature of this language.
	 */
	private Signature signature;
	
	/**
	 * Creates a new language on the given signature.
	 * @param signature a signature.
	 */
	public Language(Signature signature){
		this.signature = signature;
	}
	
	/**
	 * Checks whether the given formula is representable in this language.
	 * @param formula the formula to be checked
	 * @return "true" if the formula is representable in this language.
	 */
	public abstract boolean isRepresentable(Formula formula);
	
	/**
	 * Checks whether the given knowledge base is representable in this language.
	 * @param beliefBase the knowledge base to be checked
	 * @return "true" if the knowledge base is representable in this language.
	 */
	public abstract boolean isRepresentable(BeliefBase beliefBase);
	
	/**
	 * Returns the signature of this language.
	 * @return the signature of this language.
	 */
	public Signature getSignature(){
		return this.signature;
	}
	
}
