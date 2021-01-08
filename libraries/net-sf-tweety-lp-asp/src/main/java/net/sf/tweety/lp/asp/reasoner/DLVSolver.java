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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.writer.ClingoWriter;

/**
 * Wrapper class for the DLV answer set solver command line
 * utility.
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
	 * Additional command line options for DLV. 
	 * Default value is empty.
	 */
	private String options = "";

	/**
	 * Constructs a new instance pointing to a specific DLV solver.
	 * @param pathToDLV binary location of DLV on the hard drive
	 */
	public DLVSolver(String pathToDLV) {
		this.pathToSolver = pathToDLV;
		this.bash = Shell.getNativeShell();
	}
	
	/**
	 * Constructs a new instance pointing to a specific DLV solver.
	 * @param pathToDLV binary location of DLV on the hard drive
	 * @param bash shell to run commands
	 */
	public DLVSolver(String pathToDLV, Shell bash) {
		this.pathToSolver = pathToDLV;
		this.bash = bash;
	}
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p a program
	 * @param maxInt the max number of models to be returned
	 * @return AnswerSet
	 */
	public Collection<AnswerSet> getModels(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModels(p);
	}
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p a program
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
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file));
			writer.printProgram(p);
			writer.close();
			
			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N=" + Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData =( bash.run(cmd));	
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
			
			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N=" + Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData =( bash.run(cmd));
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
			String cmd = pathToSolver + "/dlv -silent" + " -n=" + this.maxNumOfModels + " -N=" + Integer.toString(this.integerMaximum) + " " + options + " " + file.getAbsolutePath();
			this.outputData =( bash.run(cmd));
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
	 */
	protected List<AnswerSet> parseResult(String s) {
		List<AnswerSet> result = new ArrayList<AnswerSet>();
		String[] temp = s.split("}");
		
		try {
			for (int i = 0; i < temp.length-1; i++)	{
				String toParse = temp[i].trim().substring(1).replaceAll(",", "");
				AnswerSet as = ASPCore2Parser.parseAnswerSet(toParse);
				result.add(as);
			}
			
		} catch (Exception e) {
			System.err.println("DLV error: Failed to parse answer sets from DLV output");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Set additional command line options for DLV.
	 * @param options a string of options
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Sets the location of the DLV solver on the hard drive.
	 * @param pathToDLV path to DLV
	 */
	public void setPathToDLV(String pathToDLV) {
		this.pathToSolver = pathToDLV;
	}
	
	@Override
	public Boolean query(Program beliefbase, ASPLiteral formula) {		
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

	public Boolean query(Program beliefbase, ASPLiteral formula, InferenceMode inferenceMode) {
		Collection<AnswerSet> answerSets = this.getModels(beliefbase);
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
			for(AnswerSet e: answerSets)
				if(!e.contains(formula))
					return false;
			return true;
		}
		//credulous semantics
		for(AnswerSet e: answerSets){
			if(e.contains(formula))
				return true;			
		}			
		return false;
	}

}
