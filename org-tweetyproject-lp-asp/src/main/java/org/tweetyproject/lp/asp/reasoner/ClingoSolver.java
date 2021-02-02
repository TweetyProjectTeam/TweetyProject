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
import org.tweetyproject.lp.asp.parser.ASPCore2Parser;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.writer.ClingoWriter;

/**
 * 
 * Invokes Clingo (Part of the <a href="https://potassco.org/">Potassco
 * project</a>), an ASP system that grounds and solves logic programs, and
 * returns computed answer sets.
 * 
 * @author Nils Geilen
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 */
public class ClingoSolver extends ASPSolver {
	/**
	 * String representation of Clingo binary path, meaning the location of the
	 * clingo, clasp and grinco executables on the hard drive.
	 */
	protected String pathToSolver = null;

	/**
	 * Shell to run Clingo
	 */
	private Shell bash;

	/**
	 * If activated ({@link #toggleOutputWhitelist(boolean)}), output answer sets
	 * will only contain atoms over predicates in the program's predicate whitelist.
	 * This corresponds to the #show statement of the clingo input language.
	 */
	private boolean usePredicateWhitelist = false;

	/**
	 * Additional command line options for Clingo. Default value is empty.
	 */
	private String options = "";

	/**
	 * If the program contains an optimization problem, the optimum weights (if found) are
	 * stored in this parameter.
	 */
	private String optimum = null;
	
	/**
	 * Returns the optimum of the previously solved program,
	 * if there is one.
	 * 
	 * @return optimum of previously solved program in string format, i.e. numbers separated
	 * by spaces
	 * @throws SolverException 
	 */
	public String getOptimumString() throws SolverException {
		if (optimum == null)
			throw new SolverException("Clingo found no Optimum",1);
		return optimum.strip();
	}

	/**
	 * Constructs a new instance pointing to a specific Clingo solver.
	 * 
	 * @param path2clingo binary location of Clingo on the hard drive
	 * @param bash        shell to run commands
	 */
	public ClingoSolver(String path2clingo, Shell bash) {
		this.pathToSolver = path2clingo;
		this.bash = bash;
	}

	/**
	 * Constructs a new instance pointing to specific a Clingo solver.
	 * 
	 * @param path2clingo binary location of Clingo on the hard drive
	 */
	public ClingoSolver(String path2clingo) {
		this.pathToSolver = path2clingo;
		this.bash = Shell.getNativeShell();
	}

	@Override
	public List<AnswerSet> getModels(Program p) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file), usePredicateWhitelist);
			writer.printProgram(p);
			writer.close();
			String cmd = pathToSolver + "/clingo " + options + " " + file.getAbsolutePath();
			result = parseResult(bash.run(cmd));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Computes the optimum of a program that contains optimization statements, if there is
	 * one.
	 * 
	 * Note: This methods calculates only the optimum and returns no answer sets.
	 *
	 * @param p ASP program
	 * @return list of calculated optima
	 */
	public List<Integer> getOptimum(Program p) {
		List<Integer> optima = new ArrayList<Integer>();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file), usePredicateWhitelist);
			writer.printProgram(p);
			writer.close();
			String cmd = pathToSolver + "/clingo -q " + options + " " + file.getAbsolutePath();
			String output = bash.run(cmd);
			this.outputData = output;
			if (!output.contains("OPTIMUM FOUND")) {
				this.optimum = null;
				throw new SolverException("Clingo found no optimum.", 1); 
			}
			String[] as = output.split("Optimization : ");
			this.optimum = as[1].substring(0,1);
			for (String oi : this.optimum.split("\\s"))
				optima.add(Integer.valueOf(oi));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return optima;
	}

	/**
	 * Parses output from Clingo solver to AnswerSetList.
	 * 
	 * @param output the output string
	 * @return AnswerSetList
	 * @throws SolverException if the solver had an issue
	 * @throws ParseException  if parsing failed
	 */
	private List<AnswerSet> parseResult(String output) throws SolverException, ParseException {
		this.outputData = output;
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		if (output.contains("UNSATISFIABLE"))
			return result;
		else if (!output.contains("SATISFIABLE")) {
			if (output.contains("OPTIMUM FOUND"))
				return this.parseOptimizationResult(output);
			else {
				int error_index = output.indexOf("error");
				if (error_index != -1)
					throw new SolverException("Clingo error: " + output.substring(error_index), 1);
				else
					throw new SolverException("Clingo returned no output that can be interpreted: " + output, 1);
			}
		}

		String[] as = output.split("Answer:\\s*[0-9]*\n");
		for (int i = 1; i < as.length - 1; i++) {
			AnswerSet a = ASPCore2Parser.parseAnswerSet(as[i]);
			result.add(a);
		}

		String[] final_as = as[as.length - 1].split("\n");
		AnswerSet a = ASPCore2Parser.parseAnswerSet(final_as[0]);
		result.add(a);
		return result;
	}

	/**
	 * Parses the result of a program that contains optimization statements. The optimal
	 * answer set (if found) is the first entry of the returned list. 
	 * 
	 * @param output clingo output string
	 * @return list of answer sets, the first answer set is the optimum
	 * @throws ParseException
	 */
	private List<AnswerSet> parseOptimizationResult(String output) throws ParseException {
		this.outputData = output;
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		String[] as = output.split("Answer:\\s*[0-9]*\n");
		String[] optLines = as[as.length - 1].split("\n");
		AnswerSet a = ASPCore2Parser.parseAnswerSet(optLines[0]);
		this.optimum = optLines[1].substring(optLines[1].indexOf(":") + 2);
		result.add(a);
		
		//Add other models
		for (int i = 1; i < as.length - 1; i++) {
			String [] asLines = as[i].split("\n");
			result.add(ASPCore2Parser.parseAnswerSet(asLines[0]));
		}
		
		return result;
	}

	@Override
	public List<AnswerSet> getModels(String s) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		try {
			File file = File.createTempFile("tmp", ".txt");
			PrintWriter writer = new PrintWriter(file);
			writer.write(s);
			writer.close();
			String cmd = pathToSolver + "/clingo " + options + " " + file.getAbsolutePath();
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
			String cmd = pathToSolver + "/clingo " + options + " " + file.getAbsolutePath();
			this.outputData = (bash.run(cmd));
			result = parseResult(outputData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Boolean query(Program beliefbase, ASPLiteral formula) {
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

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
	public AnswerSet getModel(Program p) {
		AnswerSet result = new AnswerSet();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file), usePredicateWhitelist);
			writer.printProgram(p);
			writer.close();
			String cmd = pathToSolver + "/clingo " + options + " 1 " + file.getAbsolutePath();
			this.outputData = (bash.run(cmd));
			result = parseResult(outputData).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Activates or deactivates the option to use a whitelist of predicates. If
	 * activated, answer sets will only contain atoms over predicates that are part
	 * of the whitelist. This corresponds to the #show statement of the clingo input
	 * language.
	 * 
	 * @param b whether to use a whitelist of predicate
	 */
	public void toggleOutputWhitelist(boolean b) {
		usePredicateWhitelist = b;
	}

	/**
	 * Set the command line options for Clingo.
	 * 
	 * @param options a string of options in the correct
	 * format, e.g. in the form "--opt" and separated
	 * by spaces
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Sets the location of the Clingo solver on the hard drive.
	 * 
	 * @param path path to Clingo
	 */
	public void setPathToDLV(String path) {
		this.pathToSolver = path;
	}

}
