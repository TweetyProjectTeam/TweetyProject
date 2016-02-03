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
package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;

import net.sf.tweety.commons.SetSignature;


/**
 * This class captures the signature of a specific
 * propositional language.
 * @author Matthias Thimm, Sebastian Homann
 */
public class PropositionalSignature extends SetSignature<Proposition> {
	
	/**
	 * Creates a new propositional signature with the given set
	 * of propositions.
	 * @param propositions a set of propositions.
	 */
	public PropositionalSignature(Collection<? extends Proposition> propositions){
		super(propositions);
	}
	
	/**
	 * Creates a new (empty) propositional signature.
	 */
	public PropositionalSignature(){
		super();
	}
	
	/**
	 * Creates a new propositional signature with the given number
	 * of propositions (named "A0"..."Anumvars".
	 * @param numvars the number of variables.
	 */
	public PropositionalSignature(int numvars){
		super();
		for(int i = 0; i < numvars; i++)
			this.add(new Proposition("A" + i));
	}
	
	/**
	 * Adds the elements of another signature to the current one.
	 * @param other a propositional signature.
	 */
	public void add(PropositionalSignature other) {
		for(Proposition p : other) {
			this.add(p);
		}
	}
}
