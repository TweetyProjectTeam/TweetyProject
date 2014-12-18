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
package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * The Tptp logic formula class
 * @author Bastian Wolf
 */
@SuppressWarnings("unused")
public abstract class TptpFofLogicFormula extends TptpFofFormula {

	/**
	 * 
	 */
	private Set<TptpFofAtomicFormula> atoms;
	
	/**
	 * 
	 */
	private Set<TptpFofFunctor> functors;
	
	
	private boolean parenthesized;
	/**
	 * 
	 */
	public TptpFofLogicFormula(){
		
	}
	
	/**
	 * 
	 * @param atoms
	 * @param functors
	 */
	public TptpFofLogicFormula(Set<TptpFofAtomicFormula> atoms,
			Set<TptpFofFunctor> functors) {
		super();
		this.atoms = atoms;
		this.functors = functors;
	}

	/*
	 * Getter
	 */
	public Set<TptpFofAtomicFormula> getAtoms() {
		return atoms;
	}

	
	public Set<TptpFofFunctor> getFunctors() {
		return functors;
	}

	
	public abstract boolean isUnitary();

	public abstract boolean isParenthesized();
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula#toString()
	 */
	public String toString(){
		// TODO implement
		return null;
	}

	
	
	
	
	
}
