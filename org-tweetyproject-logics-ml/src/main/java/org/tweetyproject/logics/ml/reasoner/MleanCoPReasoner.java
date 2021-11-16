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
package org.tweetyproject.logics.ml.reasoner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
import org.tweetyproject.logics.ml.writer.MleanCoPWriter;

/**
 * Invokes MleanCoP (<a href="http://www.leancop.de/mleancop/">http://www.leancop.de/mleancop/</a>), a compact automated theorem prover 
 * for modal first-order logic based on the clausal connection calculus. It checks whether a given formula is valid.
 * <br>
 * <b>NOTE:</b> Make sure to set the PROVER_PATH variable in the mleancop.sh script to the location
 * of the mleancop files (= the Prolog files).
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 */
public class MleanCoPReasoner extends AbstractMlReasoner {
	/**
	 * String representation of the mleancop.sh path. 
	 * This shell script specifies the prolog system, prover location and modal logic (D, T, S4 or S5)
	 * and domain (constant, cumulative or varying) that will be used.
	 * <br>
	 * <b>NOTE:</b> Make sure to set the PROVER_PATH variable in the mleancop.sh script to the location
	 * * of the mleancop files (= the prolog files).
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
	public MleanCoPReasoner(String location, Shell bash) {
		this.scriptLocation = location;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific MleanCoProver.
	 * 
	 * @param location
	 *            mleancop.sh path on the hard drive
	 */
	public MleanCoPReasoner(String location) {
		this(location,Shell.getNativeShell());
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

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.ml.reasoner.ModalReasoner#query(org.tweetyproject.logics.ml.syntax.ModalBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(MlBeliefSet beliefbase, FolFormula formula) {
		//check whether beliefbase => query is a theorem
		FolFormula complete = formula;
		for(RelationalFormula f: beliefbase)
			complete = complete.combineWithOr(new Negation(f));
		try {
			//Create input file
			File file  = File.createTempFile("tmp", "");
			file.deleteOnExit();
			MleanCoPWriter writer = new MleanCoPWriter(new PrintWriter(file,"UTF-8"));
			writer.printQuery((FolFormula) complete);
			writer.close();
		
			//Execute query
			String cmd = scriptLocation + " " + file.getAbsolutePath().replaceAll("\\\\", "/");
			String output = bash.run(cmd);
			if(Pattern.compile("is a modal [(].*[)] Theorem").matcher(output).find()) 
				return false;
			if(Pattern.compile("is a modal [(].*[)] Non-Theorem").matcher(output).find())
				return true;
			throw new RuntimeException("MleanCoP returned no result which can be interpreted.");
		} catch(Exception e){
		e.printStackTrace();
		return false; 
		}
	}

	@Override
	public boolean isInstalled() {
		try {
			bash.run(scriptLocation);
		} catch (InterruptedException | IOException e) {
			return false;
		}
		return true;
	}

}
