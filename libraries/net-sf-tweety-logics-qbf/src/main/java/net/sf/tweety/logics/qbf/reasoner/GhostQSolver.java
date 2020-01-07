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

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.qbf.parser.QdimacsParser;
import net.sf.tweety.logics.qbf.writer.QdimacsWriter;

/**
 * A wrapper for the GhostQ (<a href="https://www.wklieber.com/ghostq/">https://www.wklieber.com/ghostq/</a>) solver.
 * <br> Tested with the GhostQ 2017 version: <a href="https://www.wklieber.com/ghostq/2017.html">https://www.wklieber.com/ghostq/2017.html/</a>
 * 
 * @author Anna Gessler
 *
 */
public class GhostQSolver extends SatSolver {
	/**
	 *  String representation of the GhostQ binary path. 
	 *  Temporary files are stored in this directory.
	 */
	private String binaryLocation;

	/**
	 * Shell to run GhostQ
	 */
	private Shell bash;
	
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
	 * @return true if the result is SAT, false if the result is UNSAT
	 * @throws Exception if the bash command fails or if GhostQ produces no interpretable output
	 */
	private boolean evaluate(File file) throws Exception {
		//TODO use conversion script
		String cmd = binaryLocation + "./ghostq " + file.getAbsolutePath();
		String output = null;
		output = bash.run(cmd);
		QdimacsParser parser = new QdimacsParser();
		QdimacsParser.Answer answer = parser.parseQDimacsOutput(output);
		if (answer == QdimacsParser.Answer.SAT)
			return true;
		if (answer == QdimacsParser.Answer.UNSAT)
			return false;
		throw new RuntimeException("Failed to invoke GhostQ: GhostQ returned no result which can be interpreted.");
	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> kb) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			QdimacsWriter printer = new QdimacsWriter(new PrintWriter(file));
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
