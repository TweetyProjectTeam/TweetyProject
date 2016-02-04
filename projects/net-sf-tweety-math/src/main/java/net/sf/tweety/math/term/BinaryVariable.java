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
package net.sf.tweety.math.term;

/**
 * This class models a binary variable as a mathematical term.
 * @author Matthias Thimm
 */
public class BinaryVariable extends Variable{
	
	/**
	 * Creates a new binary variable with the given name.
	 * @param name the name of the variable
	 */
	public BinaryVariable(String name) {
		super(name,0,1);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger() {
		return true;
	}
}

