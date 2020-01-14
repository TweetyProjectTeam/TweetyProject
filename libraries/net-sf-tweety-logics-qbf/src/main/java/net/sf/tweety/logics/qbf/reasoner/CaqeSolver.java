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
package net.sf.tweety.logics.qbf.reasoner;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Pattern;

import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.qbf.writer.QdimacsWriter;

/**
 * A wrapper for the Caqe (<a href="https://www.react.uni-saarland.de/tools/caqe/">https://www.react.uni-saarland.de/tools/caqe/</a> solver.
 * <br> Tested with the 2019 version: <a href="https://github.com/ltentrup/caqe">https://github.com/ltentrup/caqe/</a>
 * 
 * @author Anna Gessler
 *
 */
public class CaqeSolver extends QbfSolver {
	/**
	 *  String representation of the CAQE binary path. 
	 */
	private String binaryLocation;

	/**
	 * Constructs a new instance pointing to a specific CaqeSolver.
	 * 
	 * @param binaryLocation
	 *            of the CAQE executable on the hard drive
	 * @param bash
	 *            shell to run commands
	 */
	public CaqeSolver(String binaryLocation, Shell bash) {
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific CaqeSolver
	 * 
	 * @param binaryLocation
	 *            of the CAQE executable on the hard drive
	 */
	public CaqeSolver(String binaryLocation) {
		this(binaryLocation, Shell.getNativeShell());
	}
	
	/**
	 * Invokes CAQE with the given input file.
	 * 
	 * @param file input file for CAQE
	 * @return true if the result is SAT, false if the result is UNSAT
	 * @throws Exception if the bash command fails or if CAQE produces no interpretable output
	 */
	private boolean evaluate(File file) throws Exception {
		String cmd = binaryLocation + "target/release/caqe --qdo " + file.getAbsolutePath();
		String output = null;
		output = bash.run(cmd);
		if (Pattern.compile("c Unsatisfiable").matcher(output).find())
			return false;
		if (Pattern.compile("c Satisfiable").matcher(output).find()) //TODO some warnings also contain "SAT"
			return true;
		throw new RuntimeException("Failed to invoke CAQE: CAQE returned no result which can be interpreted.");
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> kb) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			QdimacsWriter printer = new QdimacsWriter(new PrintWriter(file, "UTF-8"));
			printer.DISABLE_PREAMBLE_ZERO = true;
			printer.printBase((PlBeliefSet) kb);
			printer.close();
			if (evaluate(file))
				return true;
			else return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
