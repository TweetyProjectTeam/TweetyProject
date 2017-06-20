package net.sf.tweety.arg.dung.prover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.parser.AbstractDungParser;
import net.sf.tweety.arg.dung.parser.ApxParser;
import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.writer.DungWriter;

/**
 * This class implements a solver for providing the ground truth of problems.
 * The ground truth is read from external files and simply returned.
 * 
 * @author Matthias Thimm
 */
public class GroundTruthSolver extends AbstractSolver {

	/** The path to the ground truth files. */
	private String pathToGroundTruth;
	
	/**
	 * Creates a new solver that reads solutions from the given
	 * path.
	 * @param pathToGroundTruth
	 */
	public GroundTruthSolver(String pathToGroundTruth){
		this.pathToGroundTruth = pathToGroundTruth;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.probo.AbstractSolver#versionInfo()
	 */
	@Override
	public String versionInfo() {
		return "GroundTruthSolver v1.0\nMatthias Thimm (thimm@uni-koblenz.de)";
	}

	/* (non-Javadoc)
	 * @see net.sf.probo.AbstractSolver#supportedFormats()
	 */
	@Override
	public Collection<FileFormat> supportedFormats() {
		Collection<FileFormat> formats = new HashSet<FileFormat>();
		formats.add(FileFormat.APX);
		return formats;
	}

	/* (non-Javadoc)
	 * @see net.sf.probo.AbstractSolver#supportedProblems()
	 */
	@Override
	public Collection<Problem> supportedProblems() {
		Collection<Problem> problems = new HashSet<Problem>();
		for(Problem problem: Problem.values())
			problems.add(problem);
		return problems;
	}

	/* (non-Javadoc)
	 * @see net.sf.probo.AbstractSolver#solve(net.sf.probo.constants.Problem, java.io.File, net.sf.probo.constants.FileFormat, java.lang.String)
	 */
	@Override
	public String solve(Problem problem, File input, FileFormat format, String additionalParameters) throws IOException, IllegalArgumentException {
		//check whether file exists
		File extensionFile = new File(this.pathToGroundTruth + "/" + input.getName() + ".EE-" + problem.semantics().toString());
		if(!extensionFile.exists())
			throw new IllegalArgumentException("No ground truth found for the given file and semantics");
		BufferedReader in = new BufferedReader(new FileReader(extensionFile));
		String row = null;
		String all = "";
		while ((row = in.readLine()) != null)
			all += row;
		in.close();
		// the problem is EE -> just return the string
		if(problem.subProblem().equals(Problem.SubProblem.EE))
			return all;
		// otherwise, get extension list first
		Collection<Collection<Argument>> extensions = AbstractDungParser.parseExtensionList(all);
		// then solve problem directly
		if(problem.subProblem().equals(Problem.SubProblem.ES)){
			if(extensions.isEmpty()){
				// all arguments are skeptically inferred
				DungTheory theory = new ApxParser().parse(new FileReader(input));
				return DungWriter.writeArguments(theory);
			}
			Collection<Argument> result = new HashSet<Argument>(extensions.iterator().next());
			for(Collection<Argument> ext: extensions){
				result.retainAll(ext);
			}
			return DungWriter.writeArguments(result);
		}
		if(problem.subProblem().equals(Problem.SubProblem.EC)){
			Collection<Argument> result = new HashSet<Argument>();
			for(Collection<Argument> ext: extensions){
				result.addAll(ext);
			}
			return DungWriter.writeArguments(result);
		}
		// Not a problem we can solve right now
		throw new IllegalArgumentException("The given problem cannot be solved by this solver.");
	}

}
