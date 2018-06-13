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
package net.sf.tweety.lp.asp.solver;

import java.io.StringReader;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.ASTAnswerSetList;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * wrapper class for the dlv answer set solver command line
 * utility.
 * 
 * @author Thomas Vengels, Tim Janus
 *
 */
public class DLV extends SolverBase {

	String path2dlv = null;
	
	public DLV(String path2dlv) {
		this.path2dlv = path2dlv;
	}
	
	public AnswerSetList computeModels(Program p, int models) throws SolverException{
		
		return runDLV(p,models,null);
		
	}
	
	protected AnswerSetList runDLV(Program p, int nModels, String otherOptions) throws SolverException {
	
		checkSolver(path2dlv);
		String cmdLine = path2dlv + " -- " + "-N=" + nModels; 
		
		// try running dlv
		try {
			ai.executeProgram(cmdLine,p.toStringFlat());
		} catch (Exception e) {
			System.out.println("dlv error!");
			e.printStackTrace();
		}
		
		checkErrors();	
		String parseable = "";
		for(String str : ai.getOutput()) {
			if(str.trim().startsWith("{")) {
				parseable += str;
			}
		}
		return parseAnswerSets(parseable);
	}
	
	protected AnswerSetList parseAnswerSets(String s) {
		AnswerSetList ret = null;
		try {
			ASPParser parser = new ASPParser( new StringReader( s ));
			ASTAnswerSetList node = parser.AnswerSetList();
			InstantiateVisitor visitor = new InstantiateVisitor();
			ret = (AnswerSetList)node.jjtAccept(visitor, null);
		} catch (Exception e) {
			System.err.println("dlv::parseAnswerSet error");
			e.printStackTrace();
		}
		return ret;
	}

	
	@Override
	public AnswerSetList computeModels(String s, int maxModels) throws SolverException {

		String cmdLine = path2dlv + " -- " + "-N=" + maxModels; 

		checkSolver(path2dlv);
		// try running dlv
		try {
			ai.executeProgram(cmdLine,s);
		} catch (Exception e) {
			System.out.println("dlv error!");
			e.printStackTrace();
		}
		checkErrors();	
		return parseAnswerSets(s);
	}
}
