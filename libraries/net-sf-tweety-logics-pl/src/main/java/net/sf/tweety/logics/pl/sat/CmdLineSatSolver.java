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
package net.sf.tweety.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This class offers a generic wrapper for command line based SAT solvers. It is
 * likely to work for most solvers that use the Dimacs format, e.g. most solvers
 * presented at SAT solver competitions.
 * 
 * Tested with the following solvers:
 * <ul>
 * <li>CaDiCal 1.3.1 {@link (http://fmv.jku.at/cadical/)}</li>
 * <li>Kissat 1.0.3 {@link (http://fmv.jku.at/kissat/)}</li>
 * <li>Lingeling bcp {@link (http://fmv.jku.at/lingeling/)}</li>
 * <li>Slime 3.1.1 {@link https://github.com/maxtuno/slime-sat-solver}</li>
 * <ul>
 * 
 * @author Anna Gessler
 *
 */
public class CmdLineSatSolver extends SatSolver {
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
	 * @param binaryLocation
	 */
	public CmdLineSatSolver(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for (PlFormula f : formulas) {
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolver.createTmpDimacsFile(formulas, props);
			String output = NativeShell
					.invokeExecutable(this.binaryLocation + " " + options + " " + f.getAbsolutePath());
			f.delete();
			if (output.indexOf("UNSATISFIABLE") != -1)
				return null;
			// parse the model by looking for dimacs output strings, e.g. "v -1 2 3 0"
			Pattern pattern = Pattern.compile("[^a-zA-Z]v\\s(-?[1-9]+\\s)+0");
			Matcher matcher = pattern.matcher(output.trim());
			if (matcher.find()) {
				String model = output.substring(matcher.start(), matcher.end()).trim();
				model = model.replaceAll("v ", "");
				StringTokenizer tokenizer = new StringTokenizer(model, " ");
				PossibleWorld w = new PossibleWorld();
				while (tokenizer.hasMoreTokens()) {
					String s = tokenizer.nextToken().trim();
					Integer i = Integer.parseInt(s);
					if (i > 0) {
						w.add(props.get(i - 1));
					}
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
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for (PlFormula f : formulas) {
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolver.createTmpDimacsFile(formulas, props);
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
}
