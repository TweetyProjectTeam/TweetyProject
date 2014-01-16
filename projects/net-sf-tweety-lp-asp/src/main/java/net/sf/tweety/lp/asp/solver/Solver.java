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
