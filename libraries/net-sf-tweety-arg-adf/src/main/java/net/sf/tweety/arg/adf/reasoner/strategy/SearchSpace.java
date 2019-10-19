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

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Searchspace abstraction which provides methods for restricting it further.
 * 
 * @author Mathias Hofer
 *
 */
public abstract class SearchSpace {

	private AbstractDialecticalFramework adf;

	/**
	 * @param adf
	 *            the corresponding abstract dialectical framework
	 */
	public SearchSpace(AbstractDialecticalFramework adf) {
		this.adf = adf;
	}

	/**
	 * Used to explicitly notify that we are done. All subsequent calls to
	 * {@link #candidate()} must return null.
	 */
	public abstract void close();

	public abstract void updateLarger(Interpretation interpretation);

	/**
	 * Updates the searchspace s.t. we only get larger interpretations
	 * 
	 * @param interpretation
	 */
	public abstract void updateSpecificLarger(Interpretation interpretation);

	public abstract void updateUnequal(Interpretation interpretation);

	public abstract Interpretation candidate();

	/**
	 * @return the adf
	 */
	public AbstractDialecticalFramework getAbstractDialecticalFramework() {
		return adf;
	}

}
