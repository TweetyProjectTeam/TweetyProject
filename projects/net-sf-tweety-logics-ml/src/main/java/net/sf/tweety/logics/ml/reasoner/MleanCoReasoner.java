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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.ml.reasoner;

import java.io.File;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.writer.MleanCoPWriter;

/**
 * Invokes MleanCoP (<a href="http://www.leancop.de/mleancop/">http://www.leancop.de/mleancop/</a>), a compact automated theorem prover 
 * for modal first-order logic based on the clausal connection calculus. It checks whether a given formula is valid.
 * <br>
 * <b>NOTE:</b> Make sure to set the PROVER_PATH variable in the mleancop.sh script to the location
 * of the mleancop files (= the Prolog files).
 * 
 * @author Anna Gessler
 *
 */
public class MleanCoReasoner {
	/**
	 * String representation of the mleancop.sh path. 
	 * This shell script specifies the prolog system, prover location and modal logic (D, T, S4 or S5)
	 * and domain (constant, cumulative or varying) that will be used.
	 * <br><b>NOTE:</b> Make sure to set the PROVER_PATH variable in the mleancop.sh script to the location
 * of the mleancop files (= the prolog files).
	 */
	private String scriptLocation;
	
	/**
	 * Shell to run MleanCoP.
	 */
	private Shell bash;

	/**
	 * Constructs a new instance pointing to a specific MleanCoProver.
	 * 
	 * @param location
	 *            mleancop.sh path on the hard drive
	 * @param bash
	 * 			  shell to run commands
	 */
	public MleanCoReasoner(String location, Shell bash) {
		this.scriptLocation = location;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific MleanCoProver.
	 * 
	 * @param location
	 *            mleancop.sh path on the hard drive
	 */
	public MleanCoReasoner(String location) {
		this(location,Shell.getNativeShell());
	}
	
	public boolean query(Formula query) {
		try {
			//Create input file
			File file  = File.createTempFile("tmp", "");
			file.deleteOnExit();
			MleanCoPWriter writer = new MleanCoPWriter(new PrintWriter(file,"UTF-8"));
			writer.printQuery((FolFormula) query);
			writer.close();
		
			//Execute query
			String cmd = scriptLocation + " " + file.getAbsolutePath().replaceAll("\\\\", "/");
			String output = bash.run(cmd);
			if(Pattern.compile("is a modal [(].*[)] Theorem").matcher(output).find()) 
				return true;
			if(Pattern.compile("is a modal [(].*[)] Non-Theorem").matcher(output).find())
				return false;
			throw new RuntimeException("MleanCoP returned no result which can be interpreted.");
		} catch(Exception e){
		e.printStackTrace();
		return false; 
		}
	}
	
	/**
	 * Get the mleancop.sh path.
	 * @return location of MleanCOP shell script
	 */
	public String getScriptLocation() {
		return scriptLocation;
	}

	/**
	 * Set the mleancop.sh path.
	 * @param location of MleanCOP shell script
	 */
	public void setScriptLocation(String location) {
		this.scriptLocation = location;
	}

}
