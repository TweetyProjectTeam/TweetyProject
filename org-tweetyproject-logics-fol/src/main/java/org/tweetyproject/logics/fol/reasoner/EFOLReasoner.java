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
package org.tweetyproject.logics.fol.reasoner;

import java.io.File;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.writer.FolWriter;
import org.tweetyproject.logics.fol.writer.TPTPWriter;

/**
 * Invokes E (<a href="http://eprover.org">http://eprover.org</a>), 
 * an automated theorem prover for first-order logic, and returns its results.
 * 
 * @author Bastian Wolf, Nils Geilen, Matthias Thimm
 *
 */
public class EFOLReasoner extends FolReasoner {

	/**
	 *  String representation of the EProver binary path. 
	 *  Temporary files are stored in this directory.
	 */
	private String binaryLocation;
	
	/**
	 * Additional arguments for the call to the EProver binary 
	 * (Default value is "--auto-schedule" which seems to be working
	 * best in general)
	 * */
	private String additionalArguments = "--auto-schedule";
	
	/**
	 * Shell to run EProver
	 */
	private Shell bash;
	
	/**
	 * Constructs a new instance pointing to a specific EProver.
	 * @param binaryLocation location of the EProver executable on the hard drive
	 * @param bash 		     shell to run commands
	 */
	public EFOLReasoner(String binaryLocation, Shell bash) {
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}

	/**
	 * Constructs a new instance pointing to a specific EProver. 
	 * @param binaryLocation location of the Eprover executable on the hard drive
	 */
	public EFOLReasoner(String binaryLocation) {
		this(binaryLocation,Shell.getNativeShell());
	}
	
	/**
	 * Sets the additional arguments given to the call of the
	 * EProver binary (Default value is "--auto-schedule").
	 * @param s some string
	 */
	public void setAdditionalArguments(String s){
		this.additionalArguments = s;
	}

	/**
	 * Returns the additional arguments given to the call of the
	 * EProver binary (Default value is "--auto-schedule").
	 * @return the additional arguments
	 */
	public String getAdditionalArguments(){
		return this.additionalArguments;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.reasoner.FolReasoner#query(org.tweetyproject.logics.fol.syntax.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(FolBeliefSet kb, FolFormula query) {
		try{
			File file  = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();
			FolWriter printer = new TPTPWriter(new PrintWriter(file));
			printer.printBase(kb);
			printer.printQuery((FolFormula) query);
			printer.close();
			
			String cmd = binaryLocation +" " + this.additionalArguments + " --tptp3-format " + file.getAbsolutePath().replaceAll("\\\\", "/");
			String output = bash.run(cmd);

			if(Pattern.compile("# Proof found!").matcher(output).find())
				return true;
			if(Pattern.compile("# No proof found!").matcher(output).find())
				return false;
			throw new RuntimeException("Failed to invoke eprover: Eprover returned no result which can be interpreted.");
		}catch(Exception e){
			return false;
		}	
	}
	
/*
 * (non-Javadoc)
 * @see org.tweetyproject.logics.fol.prover.FolTheoremProver#equivalent(org.tweetyproject.logics.fol.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula, org.tweetyproject.logics.fol.syntax.FolFormula)
 */
	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b) {
		try{
			File file  = File.createTempFile("tmp", ".txt");
			TPTPWriter printer = new TPTPWriter(new PrintWriter(file));
			printer.printBase(kb);
			printer.printEquivalence(a,b);
			printer.close();
			
			String cmd = binaryLocation + " --tptp3-format " + file.getAbsolutePath().replaceAll("\\\\", "/");
			String output = bash.run(cmd);

			if(Pattern.compile("# Proof found!").matcher(output).find())
				return true;
			if(Pattern.compile("# No proof found!").matcher(output).find())
				return false;
			throw new RuntimeException("Failed to invoke eprover: Eprover returned no result which can be interpreted.");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}	
	}

	/**
	 * Returns the path of the EProver binary.
	 * @return the path of the EProver binary
	 */
	public String getBinaryLocation() {
		return binaryLocation;
	}

	/**
	 * Changes the path of the EProver binary.
	 * @param binaryLocation the new path of the EProver binary
	 */
	public void setBinaryLocation(String binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
}



