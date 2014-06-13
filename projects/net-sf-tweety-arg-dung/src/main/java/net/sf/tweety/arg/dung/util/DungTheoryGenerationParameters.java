/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
package net.sf.tweety.arg.dung.util;

/**
 * This class lists some parameters
 * for Dung theory generation.
 * @author Matthias Thimm
 */
public class DungTheoryGenerationParameters{
	/** The number of arguments to be created in a theory. */
	public int numberOfArguments = 20;
	/** The attack probability for each two arguments in the theory. */
	public double attackProbability = 0.5;
	/** Whether to avoid self-attacks. */
	public boolean avoidSelfAttacks = true;
	/** Whether the generated theory must have a tree shape. */
	public boolean enforceTreeShape = false;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<numberOfArguments=" + this.numberOfArguments + "," +
				"attackProbability=" + this.attackProbability + "," +
				"avoidSelfAttacks=" + this.avoidSelfAttacks + "," +
				"enforceTreeShape=" + this.enforceTreeShape +">";
	}
}