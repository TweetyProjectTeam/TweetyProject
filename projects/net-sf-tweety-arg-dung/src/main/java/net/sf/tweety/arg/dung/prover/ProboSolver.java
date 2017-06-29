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

	public ProboSolver(String path_to_exec, Shell bash) {
		super();
		this.path_to_exec = path_to_exec;
		this.bash = bash;
	}

	@Override
	public String versionInfo() {
		try {
		return bash.run(path_to_exec);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}

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
	
	public String solve(Problem problem, DungTheory aaf, FileFormat format, String additionalParameters) 
			throws IOException, IllegalArgumentException {
		File temp = File.createTempFile("aaf-", "."+format.extension());
		temp.deleteOnExit();
		
		DungWriter writer = DungWriter.getWriter(format);
		writer.write(aaf, temp);
	
		
		return solve(problem, temp, format, additionalParameters);
	}
	
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
