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
package net.sf.tweety.logics.fol.prover;

/**
 * 
 * @author Bastian Wolf
 *
 */
public class EProver extends FolTheoremProver {

	/**
	 *  String representation of the E binary path
	 */
	private String binaryLocation;
	
	/**
	 * Creates a new EProver with the E binary
	 * path given as parameter
	 * @param binaryLocation the E binary path
	 */
	public EProver(String binaryLocation){
		this.setBinaryLocation(binaryLocation);
	}

	/**
	 * returns the path of the provers binary
	 * @return the path of the provers binary
	 */
	public String getBinaryLocation() {
		return binaryLocation;
	}


	/**
	 * Change path of the binary
	 * @param binaryLocation the new path of the E binary
	 */
	public void setBinaryLocation(String binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
	
	
}
