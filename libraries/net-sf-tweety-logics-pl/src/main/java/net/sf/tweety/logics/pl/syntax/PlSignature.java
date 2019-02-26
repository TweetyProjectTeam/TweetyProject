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
package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;

import net.sf.tweety.commons.SetSignature;


/**
 * This class captures the signature of a specific
 * propositional language.
 * @author Matthias Thimm, Sebastian Homann
 */
public class PlSignature extends SetSignature<Proposition> {
	
	/**
	 * Creates a new propositional signature with the given set
	 * of propositions.
	 * @param propositions a set of propositions.
	 */
	public PlSignature(Collection<? extends Proposition> propositions){
		super(propositions);
	}
	
	/**
	 * Creates a new (empty) propositional signature.
	 */
	public PlSignature(){
		super();
	}
	
	/**
	 * Creates a new propositional signature with the given number
	 * of propositions (named "A0"..."Anumvars".
	 * @param numvars the number of variables.
	 */
	public PlSignature(int numvars){
		super();
		for(int i = 0; i < numvars; i++)
			this.add(new Proposition("A" + i));
	}
	
	/**
	 * Adds the elements of another signature to the current one.
	 * @param other a propositional signature.
	 */
	public void add(PlSignature other) {
		for(Proposition p : other) {
			this.add(p);
		}
	}
	
	/**
	 * Returns the set of atoms appearing in the given collection of formulas
	 * @param formulas a set of propositional formulas
	 * @return the signature of the formulas.
	 */
	public static PlSignature getSignature(Collection<? extends PlFormula> formulas) {
		PlSignature signature = new PlSignature();
		for(PlFormula f: formulas)
			signature.addAll(f.getAtoms());
		return signature;
	}
}
