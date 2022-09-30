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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.analysis;

 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.writer.PlWriter;

/**
 * Provides an interface to a command line based inconsistency measure analyzer
 * 
 * @author Sebastian Franke
 */
public class CmdLineImSolver extends BeliefSetInconsistencyMeasure<PlFormula>{

	/** The binary location of the binaries. */
	private String binaryLocation;
	

	
	/**
	 * Creates a new solver based on the open-wbo
	 * executable given as a parameter. 
	 * @param binaryLocation the path to the executable.
	 */
	public CmdLineImSolver(String binaryLocation){
		this.binaryLocation = binaryLocation;

	}


	/**
	 * Shell to run Prover9
	 */
	private Shell bash;


	/**
	 * @return the path of the Prover9 binaries.
	 */
	public String getBinaryLocation() {
		return binaryLocation;
	}

	/**
	 * Changes the path of the Prover9 binaries.
	 * 
	 * @param binaryLocation
	 *            the new path of the binary
	 */
	public void setBinaryLocation(String binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
	

	public boolean isInstalled() {
		try {
			String cmd = binaryLocation;
			bash.run(cmd);
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}



	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> formulas) {
		PlBeliefSet bs = new PlBeliefSet(formulas);
		PlWriter wr = new PlWriter(bs);
		try {
			File file = File.createTempFile("beliefset", ".txt");
			file.deleteOnExit();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.write(wr.writeToString());
			writer.close();
			System.out.println(wr.writeToString());
			double output = Double.parseDouble(NativeShell.invokeExecutable(
					this.binaryLocation + " " +  file.getAbsolutePath()));	
			
			return output;
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}


}