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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.opt.problem;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.math.term.OptProbElement;

/**
 * generalization of CSP
 * @author Sebastian Franke
 *
 */
public abstract class GeneralConstraintSatisfactionProblem extends  HashSet<OptProbElement>{
	/**
	 * constructor
	 */
	public GeneralConstraintSatisfactionProblem()
	{
		super();
	}
	/**
	 * 
	 * @param opts elements
	 */
	public GeneralConstraintSatisfactionProblem(Collection<? extends OptProbElement> opts)
	{
		super(opts);
	}

	/**
	 * version nr
	 */
	private static final long serialVersionUID = 1L;

}
