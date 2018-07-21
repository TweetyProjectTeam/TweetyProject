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
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.ASTAnswerSetList;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * 
 * Wrapper class for the DLV answer set solver command line
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
		
		// try running DLV
		try {
		File file = File.createTempFile("tmp", ".txt");
		file.deleteOnExit();
		String path = file.getAbsolutePath().replaceAll("\\\\", "/");
		PrintWriter writer = new PrintWriter(path);
		writer.print(p.toStringFlat());
		writer.close();

		ai.executeProgram(cmdLine,path);
		} catch (Exception e) {
			System.out.println("DLV error!");
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
	
	/**
	 * Processes a string of answer sets and returns an AnswerSetList.
	 * 
	 * @param s DLV output
	 * @return AnswerSetList
	 */
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
			File file = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();
			String path = file.getAbsolutePath().replaceAll("\\\\", "/");
			PrintWriter writer = new PrintWriter(path);
			writer.print(s);
			writer.close();
			
			ai.executeProgram(cmdLine,path);
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

	@Override
	public AnswerSetList computeModels(List<String> files, int maxModels) throws SolverException {
		checkSolver(path2dlv);
		try {			
			LinkedList<String> f2 = new LinkedList<String>(files);
			f2.addFirst(path2dlv);
			ai.executeProgram(f2, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		String parseable = "";
		for(String str : ai.getOutput()) {
			if(str.trim().startsWith("{")) {
				parseable += str;
			}
		}
		return parseAnswerSets(parseable);	
	}
}
