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
package org.tweetyproject.logics.mln.analysis;

import java.io.Serializable;

import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.mln.reasoner.AbstractMlnReasoner;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;

/**
 * This class represents an abstract coherence measure, i.e. a function
 * that measures the coherence of an MLN by comparing the probabilities for
 * the MLN's formulas with the intended ones.
 *
 * @author Matthias Thimm
 */
public abstract class AbstractCoherenceMeasure implements Serializable{

	/** Default */
	public AbstractCoherenceMeasure(){
		super();
	}

	private static final long serialVersionUID = 8888349459869328287L;

	/** Measures the coherence of the given MLN using the given reasoner.
	 * @param mln some MLN
	 * @param reasoner some reasoner
	 * @param signature a signature
	 * @return the coherence measure of the MLN.
	 */
	public abstract double coherence(MarkovLogicNetwork mln, AbstractMlnReasoner reasoner, FolSignature signature);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
}
