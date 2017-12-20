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
package net.sf.tweety.logics.fol.writer;

import java.io.IOException;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Prints out single fol formulas and full knowledge bases
 * 
 * @author Nils Geilen
 *
 */

public interface FolWriter {
	
	/**
	 * Prints formatted representation of a Query.
	 * @param query
	 *            the formula to be queried
	 */
	public void printQuery(FolFormula query) throws IOException;
	
	/**
	 * Prints an Equivalence
	 * @param a formula on one side of the equation
	 * @param b formula on one side of the equation
	 * @throws IOException
	 */
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException ;

	/**
	 * Prints formatted representation of a knowledge base.
	 * @param b
	 *            a knowledge base
	 */
	public void printBase(FolBeliefSet b) throws IOException ;
	
	/**
	 * Closes the Writer
	 * @throws IOException
	 */
	public void close() throws IOException;

}
