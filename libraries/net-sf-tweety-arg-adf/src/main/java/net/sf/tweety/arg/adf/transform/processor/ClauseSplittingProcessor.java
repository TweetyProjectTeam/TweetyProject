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
package net.sf.tweety.arg.adf.transform.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Literal;
import net.sf.tweety.arg.adf.syntax.pl.Negation;

/**
 * @author Mathias Hofer
 *
 */
public final class ClauseSplittingProcessor implements Processor<Set<Literal>, Collection<Set<Literal>>>{

	private final int maxClauseSize;
		
	/**
	 * @param maxClauseSize the maximal clause size of the generated clauses
	 */
	public ClauseSplittingProcessor(int maxClauseSize) {
		this.maxClauseSize = maxClauseSize;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.processor.Processor#process(java.lang.Object)
	 */
	@Override
	public Collection<Set<Literal>> process(Set<Literal> clause) {
		int clauseSize = clause.size();
		if (clauseSize > maxClauseSize) {
			// divide and ceil
			int numberOfSplits = (clauseSize + maxClauseSize - 1) / maxClauseSize;

			Collection<Set<Literal>> splits = new ArrayList<>(numberOfSplits);

			Set<Literal> split = null;
			Atom glue = null;
			// the number of glue variables for the current split
			// either 1 for the first and last split, or 2 for all in-between
			int glueNum = 1;

			int i = 0;
			for (Literal literal : clause) {
				// the current clause is full, therefore create a new one
				if (i % (maxClauseSize - glueNum) == 0) {
					split = new HashSet<>();

					// not the first split, therefore glue the splits together
					// before we create a new glue variable
					if (glue != null) {
						split.add(new Negation(glue));
					}

					if (i > (clauseSize - maxClauseSize)) {
						// the last split, therefore no new glue variable needed
						glueNum = 1;
					} else {
						// not the last therefore create a new glue variable
						glue = Atom.of("glue_" + i);
						split.add(glue);
						// the following clause contains 2 glue variables
						glueNum = 2;
					}

					splits.add(split);
				}

				split.add(literal);
				i++;
			}
			return splits;
		}
		// nothing to do
		return Set.of(clause);
	}

}
