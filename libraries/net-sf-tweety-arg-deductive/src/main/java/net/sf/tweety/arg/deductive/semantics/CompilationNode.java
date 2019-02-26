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
package net.sf.tweety.arg.deductive.semantics;

import java.util.Collection;

import net.sf.tweety.graphs.Node;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Instances of this class represent nodes in the compilation of
 * a knowledge base, i.e. minimal inconsistent subsets of the
 * knowledge base.
 * 
 * @author Matthias Thimm
 */
public class CompilationNode extends PlBeliefSet implements Node {
	
	/**
	 * Creates a new compilation node with the given
	 * set of formulas.
	 * @param formulas a set of formulas.
	 */
	public CompilationNode(Collection<? extends PlFormula> formulas){
		super(formulas);
	}
}
