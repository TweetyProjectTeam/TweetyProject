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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.qbf.reasoner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Pattern;

import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.qbf.writer.QdimacsWriter;

/**
 * A wrapper for the GhostQ (<a href="https://www.wklieber.com/ghostq/">https://www.wklieber.com/ghostq/</a>) solver.
 * <br> Tested with the GhostQ 2017 version: <a href="https://www.wklieber.com/ghostq/2017.html">https://www.wklieber.com/ghostq/2017.html/</a>
 * 
 * @author Anna Gessler
 *
 */
public class GhostQSolver extends QbfSolver {
	/**
	 *  String representation of the GhostQ binary path. 
	 *  Temporary files are stored in this directory.
	 */
	private String binaryLocation;
	
	/**
	 * Constructs a new instance pointing to a specific GhostQSolver.
	 * 
	 * @param binaryLocation
	 *            of the GhostQ executable on the hard drive
	 * @param bash
	 *            shell to run commands
	 */
	public GhostQSolver(String binaryLocation, Shell bash) {
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific GhostQSolver
	 * 
	 * @param binaryLocation
	 *            of the GhostQ executable on the hard drive
	 */
	public GhostQSolver(String binaryLocation) {
		this(binaryLocation, Shell.getNativeShell());
	}
	
	/**
	 * Invokes GhostQ with the given input file.
	 * 
	 * @param file input file for GhostQ
	 * @param file2 
	 * @return true if the result is SAT, false if the result is UNSAT
	 * @throws Exception if the bash command fails or if GhostQ produces no interpretable output
	 */
	private boolean evaluate(File file, File file2) throws Exception {
		String cmd_convert_file = "python " + binaryLocation + "qcir-conv.py "+ file.getAbsolutePath() +" -o " + file2.getAbsolutePath() + " -write-gq -quiet";
		bash.run(cmd_convert_file);
		String cmd = binaryLocation + "./ghostq " + file2.getAbsolutePath();
		String output = null;
		output = bash.run(cmd);
		if (Pattern.compile("false").matcher(output).find())
			return false;
		if (Pattern.compile("true").matcher(output).find()) //TODO some warnings also contain "SAT"
			return true;
		throw new RuntimeException("Failed to invoke GhostQ: GhostQ returned no result which can be interpreted.");
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> kb) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			File file2 = File.createTempFile("tmp", ".txt");
			QdimacsWriter printer = new QdimacsWriter(new PrintWriter(file, "UTF-8"));
			printer.printBase(new PlBeliefSet(kb));
			printer.close();
			if (printer.special_formula_flag != null) 
				return printer.special_formula_flag;
			if (evaluate(file,file2))
				return true;
			else return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isInstalled() {
		String cmd = binaryLocation + "target/release/ghostq --help ";
		try {
			bash.run(cmd);
		} catch (InterruptedException | IOException e) {
			return false;
		}
		return true;
	}
}
