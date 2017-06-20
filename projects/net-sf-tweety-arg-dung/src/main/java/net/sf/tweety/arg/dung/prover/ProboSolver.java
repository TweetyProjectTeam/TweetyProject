package net.sf.tweety.arg.dung.prover;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;
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
			String str = bash.run(path_to_exec);
			return null;
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}

	@Override
	public Collection<Problem> supportedProblems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String solve(Problem problem, File input, FileFormat format, String additionalParameters)
			throws IOException, IllegalArgumentException {
		try {
			String cmd=path_to_exec + " -p " + problem +" -f "+ input.toString().replaceAll("\\\\", "/");
			System.out.println(cmd);
			return bash.run(cmd);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}



}
