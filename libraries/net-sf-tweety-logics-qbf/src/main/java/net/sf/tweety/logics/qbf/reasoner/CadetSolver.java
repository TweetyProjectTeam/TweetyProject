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
 * A wrapper for the Cadet (<a href="https://markusrabe.github.io/cadet/">https://markusrabe.github.io/cadet/</a>) solver.
 * Tested with version 2.5.
 * 
 * @author Anna Gessler
 *
 */
public class CadetSolver extends QbfSolver {
	/**
	 *  String representation of the Cadet binary path. 
	 */
	private String binaryLocation;
	
	/**
	 * Constructs a new instance pointing to a specific CadetSolver.
	 * 
	 * @param binaryLocation
	 *            of the GhostQ executable on the hard drive
	 * @param bash
	 *            shell to run commands
	 */
	public CadetSolver(String binaryLocation, Shell bash) {
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific CadetSolver
	 * 
	 * @param binaryLocation
	 *            of the CadetSolver executable on the hard drive
	 */
	public CadetSolver(String binaryLocation) {
		this(binaryLocation, Shell.getNativeShell());
	}
	
	/**
	 * Invokes Cadet with the given input file.
	 * 
	 * @param file input file for Cadet
	 * @return true if the result is SAT, false if the result is UNSAT
	 * @throws Exception if the bash command fails or if Cadet produces no interpretable output
	 */
	private boolean evaluate(File file) throws Exception {
		String cmd =  binaryLocation + "./cadet " + file.getAbsolutePath();
		String output = null;
		output = bash.run(cmd);
		if (Pattern.compile("UNSAT").matcher(output).find())
			return false;
		if (Pattern.compile("SAT(\\s)*\n").matcher(output).find())
			return true;
		if (Pattern.compile("Is not 2QBF. Currently not supported.").matcher(output).find())
			throw new RuntimeException("Failed to invoke Cadet: Input is not in 2QBF, Cadet currently only supports 2QBF formulas.");
		throw new RuntimeException("Failed to invoke Cadet: Cadet returned no result which can be interpreted.");
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> kb) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			QdimacsWriter printer = new QdimacsWriter(new PrintWriter(file, "UTF-8"));
			printer.printBase(new PlBeliefSet(kb));
			printer.close();
			if (printer.special_formula_flag != null) 
				return printer.special_formula_flag;
			if (evaluate(file))
				return true;
			else return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
