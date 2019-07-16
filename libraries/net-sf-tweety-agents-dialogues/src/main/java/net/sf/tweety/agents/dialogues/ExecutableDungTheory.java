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
package net.sf.tweety.agents.dialogues;

import net.sf.tweety.agents.Executable;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * This class packs a Dung theory into an executable object.
 * 
 * @author Matthias Thimm
 */
public class ExecutableDungTheory extends DungTheory implements Executable {

	/**
	 * Creates a new empty theory.
	 */
	public ExecutableDungTheory() {
		super();
	}
	
	/**
	 * Creates a new dung theory for the given Dung theory.
	 * @param theory a Dung theory.
	 */
	public ExecutableDungTheory(DungTheory theory) {
		super(theory);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Executable#isNoOperation()
	 */
	@Override
	public boolean isNoOperation() {
		return this.isEmpty() && this.getAttacks().isEmpty();
	}
}
