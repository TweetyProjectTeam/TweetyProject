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

import java.util.List;

import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * Wrapper class for the DLV answer set solver command line
 * utility.
 * 
 * TODO
 * 
 * @author Thomas Vengels, Tim Janus, Anna Gessler
 *
 */
public class DLVSolver extends ASPSolver {
	
	private String pathToDLV;

	public DLVSolver(String pathToDLV) {
		this.setPathToDLV(pathToDLV);
	}

	@Override
	public AnswerSetList computeAnswerSets(Program p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnswerSetList computeAnswerSets(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnswerSetList computeAnswerSets(List<String> files) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathToDLV() {
		return pathToDLV;
	}

	public void setPathToDLV(String pathToDLV) {
		this.pathToDLV = pathToDLV;
	}

}
