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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.grounder;

import java.io.File;
import java.io.PrintWriter;

import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.lp.asp.parser.AspifParser;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.writer.ClingoWriter;

/**
 * Invokes Gringo (Part of the <a href="https://potassco.org/">Potassco
 * project</a>), an ASP system that grounds logic programs.
 * 
 * @author Anna Gessler
 */
public class GringoGrounder extends ASPGrounder {
	/**
	 * String representation of Gringo binary path, meaning the location of the
	 * gringo executable.
	 */
	private String pathToGrounder = null;

	/**
	 * Shell to run Gringo
	 */
	private Shell bash;

	/**
	 * Additional command line arguments for Gringo.
	 */
	private String options;

	/**
	 * Output of the previous gringo call, in aspif format.
	 */
	private String aspifOutput;
	
	public GringoGrounder(String path, Shell shell) {
		this.pathToGrounder = path;
		this.bash = shell;
		this.options = "";
	}

	public GringoGrounder(String path) {
		this.pathToGrounder = path;
		this.bash = Shell.getNativeShell();
		this.options = "";
	}

	/**
	 * @return the path to the gringo binary
	 */
	public String getPathToGrounder() {
		return pathToGrounder;
	}

	/**
	 * Set the path to the gringo binary
	 * 
	 * @param pathToGrounder
	 */
	public void setPathToGrounder(String pathToGrounder) {
		this.pathToGrounder = pathToGrounder;
	}

	/**
	 * @return additional command line options for gringo
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * Set additional command line options for gringo.
	 * 
	 * @param options
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * @return output of last gringo call, in aspif format. Can be piped directly to
	 *         clasp.
	 */
	public String getOutput() {
		return aspifOutput;
	}

	@Override
	public Program getGroundProgram(Program p) {
		Program result = new Program();
		try {
			File file = File.createTempFile("tmp", ".txt");
			ClingoWriter writer = new ClingoWriter(new PrintWriter(file), false);
			writer.printProgram(p);
			writer.close();
			String cmd = pathToGrounder + "/gringo --warn=none " + options + " " + file.getAbsolutePath();
			String output = bash.run(cmd);
			this.aspifOutput = output;

			if (output.isBlank())
				return result;
			
			AspifParser parser = new AspifParser();
			result = parser.parseProgram(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}