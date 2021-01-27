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
package org.tweetyproject.agents.dialogues;

import java.util.*;

import org.tweetyproject.agents.*;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class packs a set of formulas into an executable object.
 * 
 * @author Matthias Thimm
 */
public class ExecutableFormulaSet extends HashSet<PlFormula> implements Executable {

	/** Fpr serialization.  */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new empty set.
	 */
	public ExecutableFormulaSet() {
		super();
	}
	
	/**
	 * Creates a new set for the given formulas.
	 * @param formulas a collection of arguments.
	 */
	public ExecutableFormulaSet(Collection<? extends PlFormula> formulas) {
		super(formulas);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.Executable#isNoOperation()
	 */
	@Override
	public boolean isNoOperation() {
		return this.isEmpty();
	}
}
