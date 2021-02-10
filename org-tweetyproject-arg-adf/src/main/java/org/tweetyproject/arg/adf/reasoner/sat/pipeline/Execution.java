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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.Collection;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * Encapsulates the state of a query execution.
 * 
 * @author Mathias Hofer
 *
 */
public interface Execution extends AutoCloseable {

	Interpretation computeCandidate();
	
	boolean verify(Interpretation candidate);
	
	Interpretation processModel(Interpretation model);
	
	boolean addClause( Clause clause );
	
	/**
	 * 
	 * @param clauses
	 * @return true iff all of the clauses were successfully added
	 */
	boolean addClauses( Collection<? extends Clause> clauses);
	
	@Override
	void close();
	
}
