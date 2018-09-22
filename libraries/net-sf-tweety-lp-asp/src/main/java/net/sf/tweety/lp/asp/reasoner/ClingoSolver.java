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
package net.sf.tweety.lp.asp.reasoner;

import java.io.File;
import java.io.PrintWriter;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.writer.ClingoWriter;

/**
 * 
 * Invokes Clingo (Part of the  <a href="https://potassco.org/">Potassco project</a>), 
 * an ASP system that grounds and solves logic programs, and returns computed
 * answer sets.
 * 
 * @author Nils Geilen 
 * @author Matthias Thimm 
 * @author Anna Gessler
 *
 */
public class ClingoSolver extends ASPSolver {
	/**
	 * String representation of Clingo binary path,
	 * meaning the location of the clingo, clasp and grinco
	 * executables on the hard drive.
	 */
	protected String pathToSolver = null;
	
	/** 
	 * Shell to run Clingo
	 */
	private Shell bash;
	
	/**
	 * If activated ({@link #toggleOutputWhitelist(boolean)}),
	 * output answer sets will only
	 * contain atoms over predicates
	 * in the program's predicate whitelist.
	 * This corresponds to the #show statement
	 * of the clingo input language.
	 */
	private boolean usePredicateWhitelist = false;
	
	/**
	 * Activates or deactivates the option
	 * to use a whitelist of predicates. If activated,
	 * answer sets will only contain atoms over
	 * predicates that are part of the whitelist.
	 * This corresponds to the #show statement
	 * of the clingo input language.
	 */
	public void toggleOutputWhitelist(boolean b) {
		usePredicateWhitelist = b;
	}
	
	/**
	 * The maximum number of models to be generated.
	 */
	private int maxNumOfModels = 100;
	
	/**
	 * Get the maximum number of models to be generated.
	 * @return maximum number
	 */
	public int getMaxNumOfModels() {
		return maxNumOfModels;
	}

	/**
	 * Set the maximum number of models to be generated.
	 * @return maximum number
	 */
	public void setMaxNumOfModels(int maxNumOfModels) {
		this.maxNumOfModels = maxNumOfModels;
	}

	/**
	 * Additional command line options for Clingo. 
	 * Default value is empty.
	 */
	private String options = "";
	
	/**
	 * Constructs a new instance pointing to a specific Clingo solver.
	 * @param path2clingo binary location of Clingo on the hard drive
	 * @param bash shell to run commands
	 */
	public ClingoSolver(String path2clingo, Shell bash) {
		this.pathToSolver = path2clingo;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to specific a Clingo solver.
	 * @param path2clingo binary location of Clingo on the hard drive
	 */
	public ClingoSolver(String path2clingo) {
		this.pathToSolver = path2clingo;
		this.bash = Shell.getNativeShell();
	}
	
	@Override
	public AnswerSetList getModels(Program p) {
		AnswerSetList result = new AnswerSetList();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file),usePredicateWhitelist);
			writer.printProgram(p);
			writer.close();
			
			String cmd = pathToSolver + "/clingo " + "-n " + maxNumOfModels + " " + options + " " + file.getAbsolutePath();
			result = parseResult(bash.run(cmd));	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Parses output from Clingo solver to AnswerSetList.
	 * @param output
	 * @return AnswerSetList
	 * @throws SolverException 
	 * @throws ParseException 
	 */
	private AnswerSetList parseResult(String output) throws SolverException, ParseException {
		AnswerSetList result = new AnswerSetList();
		if (output.contains("UNSATISFIABLE"))
			return result;
		else if (!output.contains("SATISFIABLE"))
			throw new SolverException("Clingo returned no output that can be interpreted.",1);
		
		String[] as = output.split("Answer:\\s*[0-9]*\n");
		
		for (int i = 1; i < as.length-1; i++) {
			AnswerSet a = ASPCore2Parser.parseAnswerSet(as[i]);
			result.add(a);
		}
		
		String[] final_as = as[as.length-1].split("\n");
		AnswerSet a = ASPCore2Parser.parseAnswerSet(final_as[0]);
		result.add(a);
		
		return result;
	}

	@Override
	public AnswerSetList getModels(String s) {
		AnswerSetList result = new AnswerSetList();
		try {
			File file = File.createTempFile("tmp", ".txt");
			PrintWriter writer = new PrintWriter(file);
			writer.write(s);
			writer.close();
			
			String cmd = pathToSolver + "/clingo " + "-n " + maxNumOfModels + " " + options + " " + file.getAbsolutePath();
			this.outputData =( bash.run(cmd));	
			result = parseResult(outputData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Boolean query(Program beliefbase, ASPLiteral formula) {
		//TODO 
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public AnswerSet getModel(Program p) {
		AnswerSet result = new AnswerSet();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file),usePredicateWhitelist);
			writer.printProgram(p);
			writer.close();
			
			String cmd = pathToSolver + "/clingo " + "-n " + maxNumOfModels + " " + options + " " + " 1 " + file.getAbsolutePath();
			this.outputData =( bash.run(cmd));	
			result = parseResult(outputData).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
