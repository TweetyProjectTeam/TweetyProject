/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 package net.sf.tweety.arg.dung.prover;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.parser.AbstractDungParser;
import net.sf.tweety.arg.dung.parser.FileFormat;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.semantics.Problem;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.writer.DungWriter;

/**
 * This class extends <code>AbstractSolver</code> further by parsing
 * abstract argumentation theories (for all formats) into the Tweety
 * data structures. By extending this class (instead of <code>AbstractSolver</code>)
 * one has not to care about file parsing.  
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractDungSolver extends AbstractSolver {

	/* (non-Javadoc)
	 * @see argc.AbstractSolver#versionInfo()
	 */
	@Override
	public abstract String versionInfo();

	/* (non-Javadoc)
	 * @see argc.AbstractSolver#supportedProblems()
	 */
	@Override
	public abstract Collection<Problem> supportedProblems();
	
	/**
	 * Solves the problem of deciding whether an argument (given as additional parameter) is credulously inferred
	 * and returns either "true" (if it is credulously inferred) or "false" (if it is not credulously inferred)
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @param arg some argument
	 * @return "true" iff arg is credulously inferred from aaf wrt. the given semantics.
	 */
	public abstract boolean solveDC(Semantics semantics, DungTheory aaf, Argument arg);
	
	/**
	 * Solves the problem of deciding whether an argument (given as additional parameter) is skeptically inferred
	 * and returns either "true" (if it is skeptically inferred) or "false" (if it is not skeptically inferred)
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @param arg some argument
	 * @return "true" iff arg is skeptically inferred from aaf wrt. the given semantics.
	 */
	public abstract boolean solveDS(Semantics semantics, DungTheory aaf, Argument arg);
	
	/**
	 * Solves the problem of deciding whether there is labeling with the set of arguments
	 * (given as additional parameter) being the exact set of argument that is labeled in and returns either "true" (there is
	 * such a labeling) or "false" (there is no such labeling).
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @param args a collection of arguments
	 * @return "true" iff there is a labeling with the given in labelled arguments.
	 */
	public abstract boolean solveDE(Semantics semantics, DungTheory aaf, Collection<Argument> args);
	
	/**
	 * Solves the problem of deciding whether the given labeling is a valid labeling wrt.
	 * the semantics and returns either "true" (if it is valid) or "false" (it is not valid).
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @param lab a labeling
	 * @return "true" iff the given labeling is valid wrt. the semantics and framework.
	 */
	public abstract boolean solveDL(Semantics semantics, DungTheory aaf, Labeling lab);
	
	/**
	 * Solves the problem of deciding whether there exists a labeling
	 * and returns out either "true" (there is a labeling) or "false" (there is no labeling).
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return "true" iff there exists a labeling for the given semantics
	 */
	public abstract boolean solveDX(Semantics semantics, DungTheory aaf);
	
	/**
	 * Solves the problem of deciding whether there exists a labeling
	 * which labels at least one argument in and returns out either "true"
	 * (there is such a labeling) or "false" (there is no such labeling).
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return "true" iff there exists a labeling for the given semantics that
	 * 	labels at least one argument in.
	 */
	public abstract boolean solveDN(Semantics semantics, DungTheory aaf);
	
	/**
	 * Enumerates all arguments that are credulously inferred.
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return a collection of all arguments that are credulously inferred
	 */
	public abstract Collection<Argument> solveEC(Semantics semantics, DungTheory aaf);
	
	/**
	 * Enumerates all arguments that skeptically inferred.
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return a collection of arguments that are credulously inferred
	 */
	public abstract Collection<Argument> solveES(Semantics semantics, DungTheory aaf);
	
	/**
	 * Enumerates all sets for which there is a labeling that labels
	 * exactly these arguments as in.
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return a collection of collections of arguments such that for each collection
	 * there is a labeling that labels these arguments as in.
	 */
	public abstract Collection<Collection<Argument>> solveEE(Semantics semantics, DungTheory aaf);

	/**
	 * Returns one extension wrt. the given semantics.
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return a collection of arguments such that there is a labeling that labels these arguments as in. If no such extension
	 * exists this method returns "null".
	 */
	public abstract Collection<Argument> solveSE(Semantics semantics, DungTheory aaf);
	
	/**
	 * Enumerates all labelings for the given semantics of the given framework.
	 * @param semantics some semantics
	 * @param aaf the abstract argumentation framework
	 * @return a collection of all labeling of the given semantics and framework.
	 */
	public abstract Collection<Labeling> solveEL(Semantics semantics, DungTheory aaf);
	
	/* (non-Javadoc)
	 * @see argc.AbstractSolver#supportedFormats()
	 */
	@Override
	public Collection<FileFormat> supportedFormats() {
		Collection<FileFormat> formats = new HashSet<FileFormat>();
		formats.add(FileFormat.APX);
		formats.add(FileFormat.TGF);
		return formats;
	}


	
	/* (non-Javadoc)
	 * @see argc.AbstractSolver#solve(argc.constants.Problem, java.io.File, argc.constants.FileFormat, java.lang.String)
	 */
	@Override
	public String solve(Problem problem, File input, FileFormat format, String additionalParameters) throws IOException, IllegalArgumentException {
		// Note that it has already been checked whether the problem and the file format are supported,
		// so no further check necessary		
		// Parse the file into an abstract argumentation framework
		AbstractDungParser parser = AbstractDungParser.getParser(format);
		// this should not happen
		if(parser == null)
			throw new IllegalArgumentException("File format should be supported, but no parser found");
		DungTheory theory = parser.parse(new FileReader(input));
		// select the correct problem and call the corresponding method.
		if(problem.subProblem().equals(Problem.SubProblem.DX)){
			if(this.solveDX(problem.semantics(), theory))
				return "YES";
			else return "NO";			
		}
		if(problem.subProblem().equals(Problem.SubProblem.DN)){
			if(this.solveDN(problem.semantics(), theory))
				return "YES";
			else return "NO";			
		}
		if(problem.subProblem().equals(Problem.SubProblem.EC)){
			return this.solveEC(problem.semantics(), theory).toString();						
		}
		if(problem.subProblem().equals(Problem.SubProblem.ES)){
			return this.solveES(problem.semantics(), theory).toString();						
		}
		if(problem.subProblem().equals(Problem.SubProblem.EE)){
			Collection<Collection<Argument>> args = this.solveEE(problem.semantics(), theory);
			String result = "[";
			boolean first = true;
			for(Collection<Argument> ext: args){
				if(first){
					result += DungWriter.writeArguments(ext);
					first = false;
				}else result += "," + DungWriter.writeArguments(ext);
			}
			result += "]";
			return result;						
		}
		if(problem.subProblem().equals(Problem.SubProblem.SE)){
			Collection<Argument> args = this.solveSE(problem.semantics(), theory);
			if(args == null)
				return "NO";
			return DungWriter.writeArguments(args);						
		}
		if(problem.subProblem().equals(Problem.SubProblem.EL)){
			Collection<Labeling> labs = this.solveEL(problem.semantics(), theory);
			String result = "";
			for(Labeling l: labs){
				result += DungWriter.writeLabeling(l) + "\n";
			}
			return result;
		}		
		// for the remaining problems we need an additional parameter, so check whether
		// this is not null
		if(additionalParameters == null)
			throw new IllegalArgumentException("Additional parameter expected");		
		if(problem.subProblem().equals(Problem.SubProblem.DC)){
			if(this.solveDC(problem.semantics(), theory, new Argument(additionalParameters)))
				return "YES";
			else return "NO";			
		}
		if(problem.subProblem().equals(Problem.SubProblem.DS)){
			if(this.solveDS(problem.semantics(), theory, new Argument(additionalParameters)))
				return "YES";
			else return "NO";			
		}
		if(problem.subProblem().equals(Problem.SubProblem.DE)){
			if(this.solveDE(problem.semantics(), theory, AbstractDungParser.parseArgumentList(additionalParameters)))
				return "YES";
			else return "NO";			
		}
		if(problem.subProblem().equals(Problem.SubProblem.DL)){
			if(this.solveDL(problem.semantics(), theory, AbstractDungParser.parseLabeling(additionalParameters)))
				return "YES";
			else return "NO";			
		}
		throw new IllegalArgumentException("Problem unknown");
	}

}
