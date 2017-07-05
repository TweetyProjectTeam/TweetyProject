package net.sf.tweety.arg.dung.prover;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.writer.DungWriter;
import net.sf.tweety.commons.util.Shell;

public class ProboSolver extends AbstractSolver {
	
	String path_to_exec;
	private Shell bash;

	/**
	 * Constructs a new instance of ProboSolver
	 * @param path_to_exec	the path to the ICMMA-compliant solver
	 * @param bash	the shell which should be used to run the solver
	 */
	public ProboSolver(String path_to_exec, Shell bash) {
		super();
		this.path_to_exec = path_to_exec;
		this.bash = bash;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.prover.AbstractSolver#versionInfo()
	 */
	@Override
	public String versionInfo() {
		try {
		return bash.run(path_to_exec);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.prover.AbstractSolver#supportedFormats()
	 */
	@Override
	public Collection<FileFormat> supportedFormats() {
		try {
			String str = bash.run(path_to_exec + " --formats");
			return FileFormat.getFileFormats(str);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.prover.AbstractSolver#supportedProblems()
	 */
	@Override
	public Collection<Problem> supportedProblems() {
		try {
			String str = bash.run(path_to_exec + " --problems");
			return Problem.getProblems(str);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.prover.AbstractSolver#solve(net.sf.tweety.arg.dung.prover.constants.Problem, java.io.File, net.sf.tweety.arg.dung.prover.constants.FileFormat, java.lang.String)
	 */
	@Override
	public String solve(Problem problem, File input, FileFormat format, String additionalParameters)
			throws IOException, IllegalArgumentException {
		try {
			String cmd=path_to_exec + " -p " + problem + " -fo "+format+" -f "+ input.toString() + " "+ additionalParameters;
		//	System.out.println(cmd);
			return bash.run(cmd);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}
	
	/**
	 * Solves the given problem on the given AAF with possibly
	 * additional parameters.
	 * @param problem the problem type to be solved.
	 * @param aaf the AAF to be solved
	 * @param format the format of the intermediary files
	 * @param additionalParameters additional parameters for the problem (maybe "null" if no further parameters are given)
	 * @return A string representing the solution to the given problem.
	 * @throws IOException in case of errors in accessing the input file.
	 * @throws IllegalArgumentException if this solver is not able to solve the given problem or does not understand the format.
	 */
	public String solve(Problem problem, DungTheory aaf, FileFormat format, String additionalParameters) 
			throws IOException, IllegalArgumentException {
		File temp = File.createTempFile("aaf-", "."+format.extension());
		temp.deleteOnExit();
		
		DungWriter writer = DungWriter.getWriter(format);
		writer.write(aaf, temp);
	
		
		return solve(problem, temp, format, additionalParameters);
	}
	
	/**
	 * Solves the given justification problem on the given AAF
	 * @param problem the justification problem type to be solved.
	 * @param aaf the AAF to be solved
	 * @param format the format of the intermediary files
	 * @param arg the argument to be justified
	 * @return true iff arg is justified
	 * @throws IOException in case of errors in accessing the input file.
	 * @throws IllegalArgumentException if this solver is not able to solve the given problem or does not understand the format.
	 */
	public boolean justify(Problem problem, DungTheory aaf, FileFormat format, Argument arg) 
			throws IOException, IllegalArgumentException {
		if (!problem.isJustificationProblem())
			throw new RuntimeException("Fail: "+problem+"is not a justification problem.");
		String str = solve(problem, aaf, format, " -a "+arg);
		if (Pattern.matches("\\s*YES\\s*",str)) {
			return true;
		} else if (Pattern.matches("\\s*NO\\s*",str)) {
			return false;
		} else {
			throw new RuntimeException("Calling executable did not return usefull output");
		}
	}
	
	/**
	 * Solves the given enumeration problem on the given AAF with
	 * @param problem the justification problem type to be solved.
	 * @param aaf the AAF to be solved
	 * @param format the format of the intermediary files
	 * @return a set of extensions of aaf under the specified semantics
	 * @throws IOException in case of errors in accessing the input file.
	 * @throws IllegalArgumentException if this solver is not able to solve the given problem or does not understand the format.
	 */
	public Set<Extension> enumerate(Problem problem, DungTheory aaf, FileFormat format) 
			throws IOException, IllegalArgumentException {
		if (problem.isJustificationProblem())
			throw new RuntimeException("Fail: "+problem+"is not an enumeration problem.");
		String str = solve(problem, aaf, format, "");
		Set<Extension> result = new HashSet<>();
		String[] exts = str.split("\\],\\[");
		for (String ext:exts) {
			ext = ext.replace("[", "");
			ext = ext.replace("]", "");
			ext = ext.replace("\n", "");
			String[] args = ext.split(",");
			Extension e = new Extension();
			loop:for (String arg : args) {
				for (Argument a: aaf.getNodes()) {
					if (a.getName().equals(arg)) {
						e.add(a);
						continue loop;
					}
				}
			}
			result.add(e);
		}
		return result;
	}



}
