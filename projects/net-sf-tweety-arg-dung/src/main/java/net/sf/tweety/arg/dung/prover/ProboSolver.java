package net.sf.tweety.arg.dung.prover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;
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
			String cmd=path_to_exec + " -p " + problem + " -fo "+format+" -f "+ input.toString().replaceAll("\\\\", "/").replaceAll("C:", "/mnt/c");
			System.out.println(cmd);
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



}
