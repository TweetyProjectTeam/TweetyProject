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
package net.sf.tweety.arg.adf.reasoner.strategy;

import java.util.Iterator;

import net.sf.tweety.arg.adf.reasoner.ReasonerStrategy;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class ModelIterator implements Iterator<Interpretation> {

	private SearchSpace searchSpace;
	
	private ReasonerStrategy strategy;

	private Interpretation next = null;

	private boolean end = false;

	/**
	 * 
	 * @param context
	 */
	public ModelIterator(ReasonerStrategy strategy, AbstractDialecticalFramework adf) {
		this.strategy = strategy;
		this.searchSpace = strategy.createSearchSpace(adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (!end && next == null) {
			// we do not know if we have already reached the end
			next = next();
		}
		return next != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Interpretation next() {
		Interpretation result = next;
		if (result != null) {
			next = null;
			return result;
		}
		if (!end) {
			Interpretation candidate = strategy.next(searchSpace);
			if (candidate == null) {
				end = true;
				return null;
			}
			result = candidate;
		}
		return result;
	}

}
