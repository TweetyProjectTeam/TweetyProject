package net.sf.tweety.arg.dung.prover;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;

/**
 * The most general interface for solvers of argumentation problems. If you want to develop
 * a solver, better use <code>AbstractSolver</code> or even better <code>AbstractDungSolver</code>
 * as these already contain basic functionalities. 
 * 
 * @author Matthias Thimm
 */
public interface InterfaceSolver {
	
	/**
	 * Returns author and version information of the solver as a string.
	 * @return a string containing author and version information of the solver.
	 */
	public String versionInfo();
	
	/**
	 * Gives a collection view of the supported formats of this solver, cf. <code>FileFormat</code>.
	 * For a description of these formats see the handbook for the argumentation competition.
	 * @return a collection view of the supported formats of this solver.
	 */
	public Collection<FileFormat> supportedFormats();
	
	/**
	 * Gives a collection view on the supported problems of this solver, cf. <code>Problem</code>.
	 * For a description of these problems see the handbook for the argumentation competition.
	 * @return a collection view on the supported problems of this solver.
	 */
	public Collection<Problem> supportedProblems();
	
	/**
	 * Solves the given problem on the given file (represented in the given format) with possibly
	 * additional parameters.
	 * @param problem the problem type to be solved.
	 * @param input the input (an abstract argumentation graph)
	 * @param format the format of the input file
	 * @param additionalParameters additional parameters for the problem (maybe "null" if no further parameters are given)
	 * @return A string representing the solution to the given problem.
	 * @throws IOException in case of errors in accessing the input file.
	 * @throws IllegalArgumentException if this solver is not able to solve the given problem or does not understand the format.
	 */
	public String solve(Problem problem, File input, FileFormat format, String additionalParameters) throws IOException, IllegalArgumentException;
	
	/**
	 * This is the main method of the solver. It receives all command line arguments and prints its
	 * solution of System.out. An actual implementation of a solver must then only contain a main method
	 * of the form "public static void main(String[] args){ new MySolver().execute(args); }".
	 * @param args command line arguments
	 */
	public void execute(String[] args);
}
