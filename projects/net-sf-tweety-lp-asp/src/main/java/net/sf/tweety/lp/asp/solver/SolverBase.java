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
package net.sf.tweety.lp.asp.solver;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.sf.tweety.lp.asp.util.AnswerSetList;

/**
 * Base class for solver adds generic error handling code.
 * @author Tim Janus
 */
public abstract class SolverBase implements Solver {
	
	protected AspInterface ai = new AspInterface();
	
	/**
	 * proofs if the solver on the given  path is an existing file with
	 * execute permission. If this is not the case a SolverException is
	 * thrown.
	 * 
	 * @param path
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
	 * default implementation of error checking (should work for dlv package)
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
			if (msg.endsWith("syntax error.")) {
				throw new SolverException(msg, SolverException.SE_SYNTAX_ERROR);
			} else if (msg.endsWith("open input.")) {
				// We are using no input files
				//throw new SolverException(msg, SolverException.SE_CANNOT_OPEN_INPUT);
			} else {
				throw new SolverException(msg, SolverException.SE_ERROR);
			}
			
		}
	}
	
	@Override
	public AnswerSetList computeModels(List<String> files, int maxModels) throws SolverException {
		// TODO Auto-generated method stub
		return null;
	}
}
