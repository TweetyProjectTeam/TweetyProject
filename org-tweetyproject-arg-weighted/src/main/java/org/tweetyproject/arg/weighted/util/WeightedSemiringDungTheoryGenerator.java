/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.weighted.util;

import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.syntax.Attack;


/**
 * Implements a customizable weighted Dung theory generator
 * @author Sandra Hoffmann
 *
 */

import org.tweetyproject.arg.weighted.syntax.*;
import org.tweetyproject.math.algebra.Semiring;

public class WeightedSemiringDungTheoryGenerator<T> {
	
	/** The generator used to generate the argumentation framework. */
	private DungTheoryGenerator generator;
	/** The semiring used for generation. */
	private Semiring<T> semiring;
	
	/**
	 * Constructs a WeightedSemiringDungTheoryGenerator with the specified DungTheoryGenerator and Semiring.
	 *
	 * @param generator The DungTheoryGenerator to be used for generating Dung theories.
	 * @param semiring The Semiring associated with the generated weighted Dung theories.
	 * @param <T> The type of weights in the Semiring.
	 */
	public WeightedSemiringDungTheoryGenerator(DungTheoryGenerator generator, Semiring<T> semiring){
		this.generator = generator;
		this.semiring = semiring;
	}
	
	/**
	 * Generates the next WeightedArgumentationFramework using the associated DungTheoryGenerator and Semiring.
	 *
	 * @return The next WeightedArgumentationFramework with randomly assigned weights on attacks.
	 */
	public WeightedArgumentationFramework<T> next() {

		WeightedArgumentationFramework<T> AF = new WeightedArgumentationFramework<T>(semiring);
		AF.add(generator.next());
		
		for(Attack att : AF.getAttacks()) {
			AF.setWeight(att, semiring.getRandomElement());
		}
	
		return AF;
		
	}
	
	
}
