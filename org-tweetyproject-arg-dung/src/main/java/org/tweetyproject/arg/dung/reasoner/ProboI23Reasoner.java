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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.io.File;
import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.Iccma23Writer;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.commons.util.Shell;

/**
 * This reasoner makes use of an external executable for solving reasoning problems
 * in abstract argumentation. That external executable must implement the 
 * probo for ICCMA23 interface standard for argumentation solvers
 * (see https://iccma2023.github.io/rules.html).
 * 
 * @author Matthias Thimm
 *
 */
public class ProboI23Reasoner extends AbstractExtensionReasoner{
	
	/**
	 * The path to the executable 
	 */
	private String path_to_exec;
	
	/**
	 * Which shell to use. 
	 */
	private Shell bash;

	/**
	 * The semantics to be used
	 */
	private Semantics semantics;
	
	/**
	 * Constructs a new instance of ProboI23Reasoner
	 * @param path_to_exec	the path to the probo-compliant solver
	 * @param semantics The semantics to be used
	 */
	public ProboI23Reasoner(String path_to_exec, Semantics semantics) {		
		this(path_to_exec,semantics,Shell.getNativeShell());
	}
	
	/**
	 * Constructs a new instance of ProboI23Reasoner
	 * @param path_to_exec	the path to the probo-compliant solver
	 * @param semantics The semantics to be used
	 * @param bash	the shell which should be used to run the solver
	 */
	public ProboI23Reasoner(String path_to_exec, Semantics semantics, Shell bash) {		
		this.path_to_exec = path_to_exec;
		this.bash = bash;
		this.semantics = semantics;
		if(!isInstalled())
			System.err.println("The solver is not installed / callable on the given path");
	}
	
	/**
	 * Gives a collection view on the supported problems of this solver, cf. <code>Problem</code>.
	 * For a description of these problems see the handbook for the argumentation competition.
	 * @return a collection view on the supported problems of this solver.
	 */
	private Collection<ProboProblem> supportedProblems() {
		try {
			String str = bash.run(path_to_exec + " --problems");
			return ProboProblem.getProblems(str);
		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument, org.tweetyproject.commons.InferenceMode)
	 */
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
		// first check whether the solver supports the problem
		String inf;
		if(inferenceMode.equals(InferenceMode.SKEPTICAL))
			inf = "DS";
		else inf = "DC";
		ProboProblem problem = ProboProblem.getProblem(inf + "-" + this.semantics.abbreviation());
		if(!this.supportedProblems().contains(problem))
			throw new UnsupportedOperationException("Problem not supported by this probo solver.");
		// only I23 format allowed for ProboI23Reasoner
		try {
			File temp = File.createTempFile("aaf-", ".i23");
			Iccma23Writer writer = new Iccma23Writer();
			writer.write(beliefbase, temp);
			String result = this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -f " + temp.getAbsolutePath() + " -a " + writer.getArgumentId(formula));
			temp.delete();
			String[] lines = result.split(System.getProperty("line.separator"));
			return lines[0].trim().toLowerCase().startsWith("yes");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	/**
	 * 
	 * @param beliefbase a beliefbase
	 * @param formula a formula
	 * @param inferenceMode the inference mode
	 * @return the queried W
	 */
	public Pair<Boolean,Extension<DungTheory>> queryW(DungTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
		// first check whether the solver supports the problem
		String inf;
		if(inferenceMode.equals(InferenceMode.SKEPTICAL))
			inf = "DS";
		else inf = "DC";
		ProboProblem problem = ProboProblem.getProblem(inf + "-" + this.semantics.abbreviation());
		if(!this.supportedProblems().contains(problem))
			throw new UnsupportedOperationException("Problem not supported by this probo solver.");
		// only I23 format allowed for ProboI23Reasoner
		try {
			File temp = File.createTempFile("aaf-", ".i23");
			Iccma23Writer writer = new Iccma23Writer();
			writer.write(beliefbase, temp);
			String result = this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -f " + temp.getAbsolutePath() + " -a " + writer.getArgumentId(formula));
			temp.delete();			
			String[] lines = result.split(System.getProperty("line.separator"));
			Extension<DungTheory> ext = null;			
			if(lines.length>1) {
				ext = new Extension<DungTheory>();
				for(String a: lines[1].substring(2).split("\\s+"))
					if(!a.trim().equals(""))
						ext.add(writer.getArgument(Integer.parseInt(a.trim())));				
			}
			return new Pair<Boolean,Extension<DungTheory>>(lines[0].trim().toLowerCase().startsWith("yes"),ext);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		throw new UnsupportedOperationException();		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// first check whether the solver supports the problem
		ProboProblem problem = ProboProblem.getProblem("SE-" + this.semantics.abbreviation());
		if(!this.supportedProblems().contains(problem))
			throw new UnsupportedOperationException("Problem not supported by this probo solver.");
		try {
			File temp = File.createTempFile("aaf-", ".i23");
			Iccma23Writer writer = new Iccma23Writer();
			writer.write((DungTheory) bbase, temp);			
			String result = this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -f " + temp.getAbsolutePath());
			temp.delete();			
			if(result.trim().toLowerCase().equals("no"))
				return null;
			Extension<DungTheory> ext = new Extension<DungTheory>(); 
			for(String a: result.substring(2).split("\\s+"))
				if(!a.trim().equals(""))
					ext.add(writer.getArgument(Integer.parseInt(a.trim())));
			return ext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 	
	}
		
	@Override
	public boolean isInstalled() {
		try {
			@SuppressWarnings("unused")
			String str = bash.run(path_to_exec + " --formats");
			return true;
		}catch(Exception e) {
			return false;
		}
	}	
}

