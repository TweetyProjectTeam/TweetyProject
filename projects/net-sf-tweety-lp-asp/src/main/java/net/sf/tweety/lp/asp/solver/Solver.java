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
package net.sf.tweety.lp.asp.solver;

import java.util.*;

import net.sf.tweety.lp.asp.util.*;
import net.sf.tweety.lp.asp.syntax.*;

/**
 * this interface models common calls to a solver.
 * 
 * @author Thomas Vengels
 *
 */
public interface Solver {

	/**
	 * this method computes at most maxModels answer sets for a given program.
	 * 
	 * @param p
	 * @param maxModels
	 * @return A list of answer sets representing the models of the given program
	 * @throws SolverException
	 */
	public AnswerSetList	computeModels(Program p, int maxModels) throws SolverException;
	
	/**
	 * this method computes at most maxModels answer sets for a given program as a flat string.
	 * @param s
	 * @param maxModels
	 * @return A list of answer sets representing the models of the given program
	 * @throws SolverException
	 */
	public AnswerSetList	computeModels(String s, int maxModels ) throws SolverException;

	/**
	 * this method computes at most maxModels for a given program, a collection of facts,
	 * and an arbitrary number of additional programs as a file resource.
	 * 
	 * @param files
	 * @param maxModels
	 * @return A list of answer sets representing the models of union of the programs that are stored
	 * 			in the given files.
	 * @throws SolverException
	 */
	public AnswerSetList	computeModels(List<String> files, int maxModels) throws SolverException;
	
}
