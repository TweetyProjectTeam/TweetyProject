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
package net.sf.tweety.arg.dung.util;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * Class implementing this interface provide the capability
 * to generate Dung theories.
 * @author Matthias Thimm
 */
public interface DungTheoryGenerator {

	/**
	 * Generates a new Dung theory
	 * @return a Dung theory,
	 */
	public DungTheory generate();
	
	/**
	 * Generates a new Dung theory where the given argument
	 * is enforced to be in the grounded extension
	 * @param arg an argument that is enforced
	 *  to be in the grounded extension of the generated theory.
	 * @return a Dung theory,
	 */
	public DungTheory generate(Argument arg);
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
