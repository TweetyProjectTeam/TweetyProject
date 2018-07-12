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
package net.sf.tweety.lp.asp.solver;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.ASTAnswerSet;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

public class DLVComplex extends SolverBase {

	String path2dlx = null;
	
	public DLVComplex(String path2dlx) {
		this.path2dlx = path2dlx;
	}
	
	public AnswerSetList computeModels(Program p, int models) throws SolverException {
		return runDLV(p,models,null);
	}
	
	protected AnswerSetList runDLV(Program p, int nModels, String otherOptions) throws SolverException {
		checkSolver(path2dlx);
		String cmdLine = path2dlx + " -- " + "-nofdcheck " + "N=" + nModels; 
		List<String> result = null;
		
		// try running dlv
		try {
			ai.executeProgram(cmdLine, p.toStringFlat() );
			result = ai.getOutput();
		} catch (Exception e) {
			System.out.println("dlvcomplex error!");
			e.printStackTrace();
		}
		
		return processResults(result);
	}
	
	public AnswerSetList computeModels(String program, int models) throws SolverException {
		String cmdLine = path2dlx + " -- " + "-nofdcheck";
		if (models > 0)
			cmdLine += " N=" + models; 
		
		List<String> result = null;

		checkSolver(path2dlx);
		// try running dlv
		try {
			ai.executeProgram(cmdLine, program );
			result = ai.getOutput();
		} catch (IOException fnfe) {
			throw new SolverException(fnfe.getMessage(), SolverException.SE_NO_BINARY);
		} catch (Exception e) {
			System.out.println("dlvcomplex error!");
			e.printStackTrace();
		}
		
		return processResults(result);
	}
		
	protected AnswerSetList processResults(List<String> result) throws SolverException {
		AnswerSetList ret = new AnswerSetList();

		checkErrors();
		
		// early return
		if (result == null)
			return ret;
		
		// process results
		AnswerSet lastAS = null;
		for (String s : result) {
			if (s.length() <= 0)
				continue;
			System.out.println(s);
			
			// answer sets starts with a '{'
			if (s.charAt(0) == '{') {
				if (lastAS != null) {
					ret.add( new AnswerSet(lastAS,0,0));
				}
				lastAS = parseAnswerSet(s);
			}
			// answer set with weak constraints
			else if (s.startsWith("Best model")) {
				
				if (lastAS != null) {
					ret.add( new AnswerSet(lastAS,0,0));
				}
				
				s = s.substring(11);
				lastAS = parseAnswerSet(s);
			}
			else if (s.startsWith("Cost")) {
				s = s.substring(25, s.length()-2 );
				String[] wl = s.split(":");
				int weight = Integer.parseInt(wl[0]);
				int level = Integer.parseInt(wl[1]);
				ret.add(new AnswerSet(lastAS,weight,level));
				lastAS = null;
			}
		}
		
		if (lastAS != null)
			ret.add( new AnswerSet(lastAS,0,0));
		
		return ret;
	}

	
	protected AnswerSet parseAnswerSet(String s) {
		AnswerSet ret = null;
		
		try {
			ASPParser parser = new ASPParser( new StringReader( s ));
			ASTAnswerSet node = parser.AnswerSet();
			InstantiateVisitor visitor = new InstantiateVisitor();
			ret = visitor.visit(node, null);
		} catch (Exception e) {
			System.err.println("dlvcomplex::parseAnswerSet error");
			System.err.println(e);
			System.err.println(Arrays.toString(e.getStackTrace()));
		}
		
		return ret;
	}

	//TODO
	@Override
	public AnswerSetList computeModels(List<String> files, int maxModels) throws SolverException {
		throw new UnsupportedOperationException("TODO");
	}
}
