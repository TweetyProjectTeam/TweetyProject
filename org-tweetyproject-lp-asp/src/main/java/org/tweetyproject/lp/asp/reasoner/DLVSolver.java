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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.reasoner;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.writer.DLVWriter;

/**
 * Wrapper class for the DLV answer set solver command line utility.
 *
 * @author Thomas Vengels, Tim Janus, Anna Gessler
 *
 */
public class DLVSolver extends ASPSolver {
	/**
	 * String representation of DLV binary path.
	 */
	private String pathToSolver;

	/**
	 * Shell to run DLV
	 */
	private Shell bash;

	/**
	 * Additional command line options for DLV. Default value is empty.
	 */
	private String options = "";

	/**
	 * Constructs a new instance pointing to a specific DLV solver.
	 *
	 * @param pathToDLV binary location of DLV on the hard drive. The given location
	 *                  has to contain a binary called "dlv". Do not include the
	 *                  binary itself in the path.
	 */
	public DLVSolver(String pathToDLV) {
		this.pathToSolver = pathToDLV;
		this.bash = Shell.getNativeShell();
	}

	/**
	 * Constructs a new instance pointing to a specific DLV solver.
	 *
	 * @param pathToDLV binary location of DLV on the hard drive. The given location
	 *                  has to contain a binary called "dlv". Do not include the
	 *                  binary itself in the path.
	 * @param bash      shell to run commands
	 */
	public DLVSolver(String pathToDLV, Shell bash) {
		this.pathToSolver = pathToDLV;
		this.bash = bash;
	}

	/**
	 * Constructs a new instance pointing to a specific DLV solver.
	 *
	 * @param pathToDLV      binary location of Clingo on the hard drive. The given
	 *                       location has to contain a binary called "clingo". Do
	 *                       not include the binary itself in the path.
	 * @param maxNOfModels   the maximum number of models that DLV will compute.
	 * @param integerMaximum the integer maximum ("-N" parameter) that DLV will use
	 */
	public DLVSolver(String pathToDLV, int maxNOfModels, int integerMaximum) {
		this.pathToSolver = pathToDLV;
		this.bash = Shell.getNativeShell();
		this.maxNumOfModels = maxNOfModels;
		this.integerMaximum = integerMaximum;
	}

	/**
	 * Returns a characterizing model (answer set) of the given belief base using
	 * the given upper integer limit.
	 *
	 * @param p      a program
	 * @param maxInt the max number of models to be returned
	 * @return AnswerSet
	 */
	public Collection<AnswerSet> getModels(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModels(p);
	}

	/**
	 * Returns a characterizing model (answer set) of the given belief base using
	 * the given upper integer limit.
	 *
	 * @param p      a program
	 * @param maxInt the max number of models to be returned
	 * @return AnswerSet
	 */
	public AnswerSet getModel(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModel(p);
	}

	@Override
	public List<AnswerSet> getModels(Program p) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		try {
			File file = File.createTempFile("tmp", ".txt");
			DLVWriter writer = new DLVWriter(new PrintWriter(file));
			writer.printProgram(p);
			writer.close();

			for (String o : p.getAdditionalOptions()) {
				if (o.startsWith("#maxint")) {
					try {
						String integerMaximum = o.substring(o.indexOf("=") + 1).strip();
						this.integerMaximum = Integer.parseInt(integerMaximum);
					} catch (NumberFormatException e) {
						System.err.println(
								"Warning: Failed to parse #maxint statement in program. Using default integer maximum "
										+ this.integerMaximum);
					}
				}
			}

			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N="
					+ Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData = (bash.run(cmd));
			result = parseResult(outputData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<AnswerSet> getModels(String p) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		try {
			File file = File.createTempFile("tmp", ".txt");
			PrintWriter writer = new PrintWriter(file);
			writer.write(p);
			writer.close();

			if (p.contains("#maxint")) {
				String o = p.substring(p.indexOf("#maxint"));
				o = p.substring(0, p.indexOf("."));
				try {
					String integerMaximum = o.substring(o.indexOf("=") + 1).strip();
					this.integerMaximum = Integer.parseInt(integerMaximum);
				} catch (NumberFormatException e) {
					System.err.println(
							"Warning: Failed to parse #maxint statement in program. Using default integer maximum "
									+ this.integerMaximum);
				}
			}

			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N="
					+ Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData = (bash.run(cmd));
			result = parseResult(outputData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<AnswerSet> getModels(File file) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		try {
			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N="
					+ Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData = (bash.run(cmd));
			result = parseResult(outputData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public AnswerSet getModel(Program p) {
		return this.getModels(p).iterator().next();
	}

	/**
	 * Processes a string containing answer sets and returns an AnswerSetList.
	 *
	 * @param s String containing DLV output
	 * @return AnswerSet
	 * @throws SolverException error
	 */
	protected List<AnswerSet> parseResult(String s) throws SolverException {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		String[] temp = s.split("}");

		if (s.contains("errors")) {
			throw new SolverException("DLV error: " + s, 1);
		}
		try {
			for (int i = 0; i < temp.length - 1; i++) {
				// DLV answer sets consist of literals separated by commas
				// Remove commas that are not inside parentheses to achieve the format
				// expected by ASPParser (literals separated by spaces)
				String toParse = temp[i].trim().substring(1).replaceAll(",(?![^()]*\\))", "");
				AnswerSet as = ASPParser.parseAnswerSet(toParse);
				result.add(as);
			}
		} catch (Exception e) {
			throw new SolverException("DLV returned no output that can be interpreted: " + s, 1);
		}
		return result;
	}

	/**
	 * Set additional command line options for DLV.
	 *
	 * @param options a string of options
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Sets the location of the DLV solver on the hard drive.
	 *
	 * @param pathToDLV path to DLV
	 */
	public void setPathToDLV(String pathToDLV) {
		this.pathToSolver = pathToDLV;
	}

	@Override
	public Boolean query(Program beliefbase, ASPLiteral formula) {
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

	/**
	 * Evaluates a query on the given belief base using the specified inference
	 * mode.
	 * <p>
	 * This method checks whether the given formula is entailed by the belief base
	 * under the specified inference mode. It retrieves the answer sets of the
	 * belief base
	 * and checks the presence of the formula in these sets based on the chosen
	 * inference
	 * mode, either skeptical or credulous.
	 * </p>
	 *
	 * @param beliefbase    The program representing the belief base to query.
	 * @param formula       The formula (literal) to be checked within the answer
	 *                      sets.
	 * @param inferenceMode The mode of inference, either skeptical or credulous.
	 * @return {@code true} if the formula is entailed by the belief base under the
	 *         specified inference mode, {@code false} otherwise.
	 */
	public Boolean query(Program beliefbase, ASPLiteral formula, InferenceMode inferenceMode) {
		Collection<AnswerSet> answerSets = this.getModels(beliefbase);
		if (inferenceMode.equals(InferenceMode.SKEPTICAL)) {
			for (AnswerSet e : answerSets)
				if (!e.contains(formula))
					return false;
			return true;
		}
		// credulous semantics
		for (AnswerSet e : answerSets) {
			if (e.contains(formula))
				return true;
		}
		return false;
	}

	@Override
	public boolean isInstalled() {

		try {

			String cmd = pathToSolver + "/dlv";
			this.outputData = (bash.run(cmd));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

}
