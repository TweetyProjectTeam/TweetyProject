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
package org.tweetyproject.arg.dung.reasoner;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.parser.AbstractDungParser;
import org.tweetyproject.arg.dung.parser.FileFormat;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.AbstractDungWriter;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.Shell;

/**
 * This reasoner makes use of an external executable for solving reasoning problems
 * in abstract argumentation. That external executable must implement the 
 * probo interface standard for argumentation solvers (see argumentationcompetition.org).
 * 
 * @author Matthias Thimm
 * @author Nils Geilen
 *
 */
public class ProboReasoner extends AbstractExtensionReasoner{
	
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
	 * Constructs a new instance of ProboReasoner
	 * @param path_to_exec	the path to the probo-compliant solver
	 * @param semantics The semantics to be used
	 */
	public ProboReasoner(String path_to_exec, Semantics semantics) {		
		this(path_to_exec,semantics,Shell.getNativeShell());
	}
	
	/**
	 * Constructs a new instance of ProboReasoner
	 * @param path_to_exec	the path to the probo-compliant solver
	 * @param semantics The semantics to be used
	 * @param bash	the shell which should be used to run the solver
	 */
	public ProboReasoner(String path_to_exec, Semantics semantics, Shell bash) {		
		this.path_to_exec = path_to_exec;
		this.bash = bash;
		this.semantics = semantics;
	}
	
	/**
	 * Gives a collection view of the supported formats of this solver, cf. <code>FileFormat</code>.
	 * For a description of these formats see the handbook for the argumentation competition.
	 * @return a collection view of the supported formats of this solver.
	 */
	private Collection<FileFormat> supportedFormats() {
		try {
			String str = bash.run(path_to_exec + " --formats");
			return FileFormat.getFileFormats(str);
		}catch(Exception e) {
			throw new RuntimeException("Error calling executable "+path_to_exec);
		}
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
		FileFormat format;
		if(this.supportedFormats().contains(FileFormat.TGF))
			format = FileFormat.TGF;
		else if(this.supportedFormats().contains(FileFormat.APX))
			format = FileFormat.APX;
		else format = this.supportedFormats().iterator().next();
		try {
			File temp = File.createTempFile("aaf-", "." + format.extension());
			AbstractDungWriter writer = AbstractDungWriter.getWriter(format);
			writer.write(beliefbase, temp);
			String result = this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -fo " + format.toString() + " -f " + temp.getAbsolutePath() + " -a " + formula.getName());
			temp.delete();
			return AbstractDungParser.parseBoolean(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(DungTheory bbase) {
		// first check whether the solver supports the problem
		ProboProblem problem = ProboProblem.getProblem("EE-" + this.semantics.abbreviation());
		if(!this.supportedProblems().contains(problem))
			throw new UnsupportedOperationException("Problem not supported by this probo solver.");
		FileFormat format;
		if(this.supportedFormats().contains(FileFormat.TGF))
			format = FileFormat.TGF;
		else if(this.supportedFormats().contains(FileFormat.APX))
			format = FileFormat.APX;
		else format = this.supportedFormats().iterator().next();
		try {
			File temp = File.createTempFile("aaf-", "." + format.extension());
			AbstractDungWriter writer = AbstractDungWriter.getWriter(format);
			writer.write(bbase, temp);
			Collection<Extension> result = new HashSet<Extension>();			
			for(Collection<Argument> ext: AbstractDungParser.parseExtensionList(this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -fo " + format.toString() + " -f " + temp.getAbsolutePath())))
				result.add(new Extension(ext));
			temp.delete();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 			
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		// first check whether the solver supports the problem
		ProboProblem problem = ProboProblem.getProblem("SE-" + this.semantics.abbreviation());
		if(!this.supportedProblems().contains(problem))
			throw new UnsupportedOperationException("Problem not supported by this probo solver.");
		FileFormat format;
		if(this.supportedFormats().contains(FileFormat.TGF))
			format = FileFormat.TGF;
		else if(this.supportedFormats().contains(FileFormat.APX))
			format = FileFormat.APX;
		else format = this.supportedFormats().iterator().next();
		try {
			File temp = File.createTempFile("aaf-", "." + format.extension());
			AbstractDungWriter writer = AbstractDungWriter.getWriter(format);
			writer.write(bbase, temp);			
			String result = this.bash.run(this.path_to_exec + " -p " + problem.toString() + " -fo " + format.toString() + " -f " + temp.getAbsolutePath());			
			temp.delete();			
			if(result.trim().toLowerCase().equals("no"))
				return null;			
			return AbstractDungParser.parseArgumentList(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 	
	}
}
