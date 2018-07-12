/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logicprogramming.asp.solver;

import java.util.*;
import net.sf.tweety.logicprogramming.asp.syntax.*;
import java.io.*;

/**
 * this class models an input source for
 * asp solvers. an input source is either
 * a program, a collection of facts, or
 * a file (program on hard drive).
 * 
 * @author Thomas Vengels
 *
 */
public class ELPSource {
	
	public static final int SRC_FILE = 1;
	public static final int SRC_ELP = 2;
	public static final int SRC_FACT = 3;

	String					resFile;
	ELP						resELP;
	Collection<? extends ELPLiteral>	resFacts;
	int						resType;
	
	/**
	 * this method constructs an elpsource based
	 * on a filename.
	 * 
	 * @param fileName path and file of program
	 */
	public ELPSource(String fileName) {
		this.resFile = fileName;
		this.resType = ELPSource.SRC_FILE;
	}
	
	/**
	 * this method constructs an elpsource based
	 * on an elp instance.
	 * 
	 * @param elpSrc source program
	 */
	public ELPSource(ELP elpSrc) {
		this.resELP = elpSrc;
		this.resType = ELPSource.SRC_ELP;
	}
	
	/**
	 * this method constructs an elpsource based
	 * on a collection of facts.
	 * 
	 * @param factSrc collection of facts
	 */
	public ELPSource(Collection<? extends ELPLiteral> factSrc) {
		this.resFacts = factSrc;
		this.resType = ELPSource.SRC_FACT;
	}
	
	/**
	 * returns the type (ELP,FACTS,FILE) of the
	 * elp source.
	 * 
	 * @return type identifier
	 */
	public int getType() {
		return this.resType;
	}
	
	/**
	 * returns the path string of a program, if
	 * that source refers to a hard drive resource.
	 * 
	 * @return path to program
	 */
	public String getSrcFile() {
		return this.resFile;
	}
	
	/**
	 * adds a source (either elp oder facts) to
	 * a buffered writer.
	 *  
	 * @param bw
	 * @throws IOException
	 */
	public void addResource(BufferedWriter bw) throws IOException {
		if (this.resType == ELPSource.SRC_FACT)
			addFacts(bw);
		else if (this.resType == ELPSource.SRC_ELP)
			bw.write(this.resELP.toString());
	}

	/**
	 * helper function to add facts to an output stream
	 * 
	 * @param bw buffered writer output target
	 * @throws IOException
	 */
	private void addFacts(BufferedWriter bw) throws IOException {
		for (ELPLiteral l : this.resFacts) {
			String output = l.toString() + ".";
			bw.write(output);
			bw.newLine();
		}
	}
}
