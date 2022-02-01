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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * This class offers a generic wrapper for command line based SAT solvers. It is
 * likely to work for most solvers that use the Dimacs format, e.g. most solvers
 * presented at SAT solver competitions.
 * 
 * Tested with the following solvers:
 * <ul>
 * <li>CaDiCal 1.3.1 <a href="http://fmv.jku.at/cadical">http://fmv.jku.at/cadical</a></li>
 * <li>Kissat 1.0.3 <a href="http://fmv.jku.at/kissat">http://fmv.jku.at/kissat</a></li>
 * <li>Lingeling bcp <a href="http://fmv.jku.at/lingeling">href="http://fmv.jku.at/lingeling</a></li>
 * <li>Slime 3.1.1 <a href="https://github.com/maxtuno/slime-sat-solver">https://github.com/maxtuno/slime-sat-solver</a></li>
 * </ul>
 * 
 * @author Anna Gessler
 *
 */
public class CmdLineSatSolver extends DimacsSatSolver {
	/**
	 * The binary location of the solver.
	 */
	protected String binaryLocation = null;

	/**
	 * Command line options for the solver. Note: Available options can be found in
	 * the manuals of the respective solvers.
	 */
	private String options = "";

	/**
	 * Creates a new SAT solver based on the given binary location.
	 * 
	 * @param binaryLocation the location of the binary
	 */
	public CmdLineSatSolver(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, Map<Integer,Proposition> prop_inverted_index, String additional_clauses, int num_additional_clauses) {
		try {			
			// create temporary file in Dimacs CNF format.
			File f = DimacsSatSolver.createTmpDimacsFile(formulas, prop_index, additional_clauses, num_additional_clauses);
			String output = NativeShell
					.invokeExecutable(this.binaryLocation + " " + options + " " + f.getAbsolutePath());
			f.delete();
			if (output.indexOf("UNSATISFIABLE") != -1)
				return null;
			// parse the model by looking for dimacs output string ("v -1 2 3 0")
			if (output.indexOf("\nv") > -1) {
				String model = output.substring(output.indexOf("\nv")+1);
				model = model.substring(0, model.indexOf(" 0")).trim();
				model = model.replaceAll("v", "");				
				StringTokenizer tokenizer = new StringTokenizer(model, " ");
				PossibleWorld w = new PossibleWorld();
				while (tokenizer.hasMoreTokens()) {
					String s = tokenizer.nextToken().trim();
					Integer i = Integer.parseInt(s);
					if (i > 0) {
						w.add(prop_inverted_index.get(i));
					}else if(i == 0)
						break;
				}
				return w;
			} else
				throw new IllegalArgumentException(
						"Unable to find witness in solver output. Depending on your solver, you may need to add a cmd line option like --W to enable it.");
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, String additional_clauses, int num_additional_clauses) {
		try {			
			// create temporary file in Dimacs CNF format.
			File f = DimacsSatSolver.createTmpDimacsFile(formulas, prop_index, additional_clauses, num_additional_clauses);
			String output = NativeShell
					.invokeExecutable(this.binaryLocation + " " + options + " " + f.getAbsolutePath());
			// delete file
			f.delete();
			return (output.indexOf("UNSATISFIABLE") == -1);
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a single command line parameter. Needs to be in the correct format,
	 * usually in the form "--opt". Note: Available options can be found in the
	 * manuals of the respective solvers.
	 * 
	 * @param option string
	 */
	public void addOption(String option) {
		options += " " + option.strip() + " ";
	}

	/**
	 * Sets the options parameter of the sat solver. Needs to be in the correct
	 * format, usually in the form "--opt1 --opt2", with spaces between different
	 * options. This replaces the whole options string, replacing any previously
	 * added parameters or default parameters.
	 * 
	 * Note: Available options can be found in the manuals of the respective
	 * solvers.
	 * 
	 * @param options string of options
	 */
	public void setOptions(String options) {
		this.options = options.strip();
	}

	@Override
	public boolean isInstalled() {
		try {
			NativeShell.invokeExecutable(this.binaryLocation + " -h");
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}
