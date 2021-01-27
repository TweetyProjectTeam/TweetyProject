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
import org.tweetyproject.logics.fol.writer.Prover9Writer;

/**
 * Invokes Prover9 
 * (<a href="https://www.cs.unm.edu/~mccune/mace4/"> https://www.cs.unm.edu/~mccune/mace4/</a>),
 * an automated theorem prover for first-order logic, and returns its results.
 * 
 * @author Nils Geilen
 * @author Matthias Thimm
 *
 */

public class Prover9FolReasoner extends FolReasoner {

	/**
	 *  String representation of the EProver binary path. 
	 *  Temporary files are stored in this directory.
	 */
	private String binaryLocation;

	/**
	 * Shell to run Prover9
	 */
	private Shell bash;

	/**
	 * Constructs a new instance pointing to a specific Prover9.
	 * 
	 * @param binaryLocation
	 *            of the prover9 executable on the hard drive
	 * @param bash
	 *            shell to run commands
	 */
	public Prover9FolReasoner(String binaryLocation, Shell bash) {
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}

	/**
	 * Constructs a new instance pointing to a specific Prover9
	 * 
	 * @param binaryLocation
	 *            of the prover9 executable on the hard drive
	 */
	public Prover9FolReasoner(String binaryLocation) {
		this(binaryLocation, Shell.getNativeShell());
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.reasoner.FolReasoner#query(org.tweetyproject.logics.fol.syntax.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(FolBeliefSet kb, FolFormula query) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			Prover9Writer printer = new Prover9Writer(new PrintWriter(file));
			printer.printBase(kb);
			printer.printQuery((FolFormula) query);
			printer.close();
			if (eval(file))
				return true;
			else return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.fol.prover.FolTheoremProver#equivalent(org.tweetyproject
	 * .logics.fol.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula,
	 * org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b) {
		try {
			File file = File.createTempFile("tmp", ".txt");
			Prover9Writer printer = new Prover9Writer(new PrintWriter(file));
			printer.printBase(kb);
			printer.printEquivalence(a, b);
			printer.close();
			return eval(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invokes Prover9.
	 * 
	 * @param file
	 *            input file for Prover9
	 * @return query result
	 * @throws Exception if something goes wrong
	 */
	private boolean eval(File file) throws Exception {
		String cmd = binaryLocation + " -f " + file.getAbsolutePath();
		//System.out.println(cmd);
		String output = null;
		output = bash.run(cmd);
		// output = Exec.invokeExecutable(cmd, -1, true);
		//System.out.print(output);
		if (Pattern.compile("Exiting with .+ proof").matcher(output).find())
			return true;
		if (Pattern.compile("Exiting with failure").matcher(output).find())
			return false;
		throw new RuntimeException("Failed to invoke prover9: Prover9 returned no result which can be interpreted.");
	}

	/**
	 * Returns the path of the Prover9 binaries.
	 * 
	 * @return binary location of Prover9
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

}
