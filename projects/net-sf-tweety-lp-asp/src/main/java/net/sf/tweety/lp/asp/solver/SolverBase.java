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
package net.sf.tweety.lp.asp.solver;

import java.io.File;
import java.util.Iterator;

/**
 * Base class for solver adds generic error handling code.
 * @author Tim Janus
 */
public abstract class SolverBase implements Solver {
	
	protected AspInterface ai = new AspInterface();
	
	/**
	 * Checks if the solver in the given  path is an existing file with
	 * execute permission. If this is not the case, a SolverException is
	 * thrown.
	 * 
	 * @param path a String containing a path to a solver
	 * @throws SolverException
	 */
	protected void checkSolver(String path) throws SolverException {
		File solverBin = new File(path);
		if(!solverBin.isFile()) {
			throw new SolverException("'" + path + "' is no file.", 
					SolverException.SE_CANNOT_FIND_SOLVER);
		} else if(!solverBin.canExecute()) {
			throw new SolverException("No permission to execute: '" + path + "'", 
					SolverException.SE_PERMISSIONS);
		}
	}
	
	/**
	 * Default implementation of error checking (should work for DLV package)
	 * @throws SolverException
	 */
	protected void checkErrors() throws SolverException {
		// early exit
		if (ai.getError().size() > 0) {
			Iterator<String> iter = ai.getError().iterator();
			String msg = "";
			while (iter.hasNext()) {
				msg += iter.next();
			}
			
			if (msg.endsWith("syntax error.")) 
				throw new SolverException(msg, SolverException.SE_SYNTAX_ERROR);
			else if (msg.endsWith("open input.")) 
				throw new SolverException(msg, SolverException.SE_CANNOT_OPEN_INPUT);
			else 
				throw new SolverException(msg, SolverException.SE_ERROR);

		}
	}
}
