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
package org.tweetyproject.logics.ml.reasoner;

import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;

/**
 * Abstract modal reasoner to be implemented by concrete reasoners.
 *
 * @author Bastian Wolf
 * @author Nils Geilen
 * @author Anna Gessler
 * @author Matthias Thimm
 *
 */
public abstract class AbstractMlReasoner implements QualitativeReasoner<MlBeliefSet,FolFormula> {

    /**
	 * Default Constructor
	 */
	public AbstractMlReasoner() {
	}


	/**
	 * Empty default prover
	 */
	public static AbstractMlReasoner defaultReasoner = null;

	/**
	 * Set default modal reasoner with given
	 * @param reasoner an ML Reasoner
	 */
	public static void setDefaultReasoner(AbstractMlReasoner reasoner){
		AbstractMlReasoner.defaultReasoner = reasoner;
	}

	/**
	 * Returns the default reasoner for modal logic
	 * @return the default modal reasoner
	 */
	public static AbstractMlReasoner getDefaultReasoner(){
		if(AbstractMlReasoner.defaultReasoner != null){
			return AbstractMlReasoner.defaultReasoner;
		} else{
			System.err.println("No default modal reasoner configured, using "
					+ "'NaiveModalReasoner' with default settings as fallback.");
			return new SimpleMlReasoner();
		}
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public abstract Boolean query(MlBeliefSet beliefbase, FolFormula formula);
}
