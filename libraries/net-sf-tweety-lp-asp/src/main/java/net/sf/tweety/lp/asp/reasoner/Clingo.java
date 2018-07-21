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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.reasoner;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

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
public class Clingo extends SolverBase {

	protected String path2clingo = null;
	
	public Clingo(String path2clingo) {
		this.path2clingo = path2clingo;
	}
	
	
	/**
	 * Uses ASPParser to parse answer sets from string. 
	 * @param String containing answer set
	 * @return AnswerSetList containing the parsed answer sets
	 */
	private AnswerSetList parseAnswerSets(String s) {		
		try {
			ASPParser ep = new ASPParser( new StringReader( s ));
			InstantiateVisitor visitior = new InstantiateVisitor();
			return visitior.visit(ep.AnswerSetList(), null);
		} catch (Exception e) {
			System.err.println("ERROR: Failed to parse answer sets from clingo output");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AnswerSetList computeModels(Program p, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {
			File file  = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();
			String path = file.getAbsolutePath().replaceAll("\\\\", "/");
			PrintWriter writer = new PrintWriter(path);
			writer.print(p.toStringFlat());
			writer.close();
		
			ai.executeProgram( path2clingo+" "+maxModels+" --verbose=0 ", path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		List<String> output = ai.getOutput(), list = new ArrayList<>();
		
		for (String str:output)
			list.add(str);
		
		return this.buildASL(list);
	}

	
	@Override
	public AnswerSetList computeModels(String s, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {
			File file  = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();
			String path = file.getAbsolutePath().replaceAll("\\\\", "/");
			PrintWriter writer = new PrintWriter(path);
			writer.print(s);
			writer.close();
			
			ai.executeProgram( path2clingo+" "+maxModels+" --verbose=0 ", path );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		return this.buildASL(ai.getOutput());
	}

	
	@Override
	public AnswerSetList computeModels(List<String> files, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {			
			LinkedList<String> f2 = new LinkedList<String>(files);
			f2.addFirst(path2clingo);
			ai.executeProgram(f2, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		return this.buildASL(ai.getOutput());				
	}
	
	
	@Override
	protected void checkErrors() throws SolverException {
		// process possible errors and throw exception
		if (ai.getError().size() > 0) {
			// skip any warning, anything else is critical!
			Iterator<String> iter = ai.getError().iterator();
			while (iter.hasNext()) {
				String l = iter.next();
				
				if (l.startsWith("% warning"))
					; // TODO: Find a warning policy like logging it at least.
				
				if (l.startsWith("ERROR:")) {
					if (iter.hasNext())
						l += iter.next();
					
					throw new SolverException( l, SolverException.SE_ERROR );
				}
					
			}
		}
	}
	
	/**
	 * Processes clingo output and returns a list of answer sets.
	 * 
	 * @param output clingo output lines
	 * @return an AnswerSetList
	 */
	protected AnswerSetList buildASL(List<String> output) {
		// First convert in dlv format:
		String toParse = "";
		for(String line : output) {
			if(line.trim().toLowerCase().equals("satisfiable"))
				continue;
			if(line.trim().toLowerCase().equals("unsatisfiable"))
				return new AnswerSetList();
			String as = line.replace(' ', ',');
			as = "{" + as + "}";
			as = as.replace(",}", "}");
			toParse += as;
		}
		return parseAnswerSets(toParse);
	}
}
