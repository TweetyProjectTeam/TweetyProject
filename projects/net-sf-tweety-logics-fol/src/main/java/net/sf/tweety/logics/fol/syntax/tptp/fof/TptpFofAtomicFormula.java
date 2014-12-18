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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class models an atom for the Tptp-Format
 * Each atom represents only one lower-case word.
 * @author Bastian Wolf
 */
public class TptpFofAtomicFormula extends TptpFofUnitaryFormula {
	
	/**
	 * The predicate of this atom
	 */
	private TptpFofPredicate predicate;
    
	/**
	 * The arguments
	 */
	private List<TptpFofTerm<?>> arguments = new ArrayList<TptpFofTerm<?>>();
	

	
	/**
	 * Empty Constructor
	 */
	public TptpFofAtomicFormula(){
		
	}
	/**
	 * Constructor with given identifier name
	 * @param predicate the predicate of this atom
	 */
	public TptpFofAtomicFormula(TptpFofPredicate predicate){
		this(predicate,new ArrayList<TptpFofTerm<?>>());
	}

	
	public TptpFofAtomicFormula(TptpFofPredicate predicate, TptpFofTerm<?>... terms){
		this(predicate, Arrays.asList(terms));
	}
	
	public TptpFofAtomicFormula(TptpFofPredicate predicate, List<? extends TptpFofTerm<?>> arguments){
		this.predicate = predicate;
		for(TptpFofTerm<?> t : arguments)
			this.addArgument(t);
	}

	
	/*
	 * Getter
	 */

	public Set<TptpFofAtomicFormula> getAtoms(){
		HashSet<TptpFofAtomicFormula> atoms = new HashSet<TptpFofAtomicFormula>();
		atoms.add(this);
		return atoms;
	}
	
	
	
	public void addArgument(TptpFofTerm<?> term) throws IllegalArgumentException{
		if(this.arguments.size() == this.predicate.getArity())
			throw new IllegalArgumentException("No more arguments expected");
//		if(this.predicate.get)
		
		this.arguments.add(term);
	}
	
	/**
	 * This method validates, if the given identifier string meets the name conventions for atoms
	 */
	public boolean validate(){
		// TODO implement validation for correct TptpFofAtomicFormula-identifier (name)
		return true;
	}
	
	@Override
	public boolean isParenthesized() {
		
		return false;
	}
	
	
}
