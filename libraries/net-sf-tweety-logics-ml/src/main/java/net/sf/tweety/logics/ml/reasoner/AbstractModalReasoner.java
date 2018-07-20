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
package net.sf.tweety.logics.ml.reasoner;

import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.syntax.ModalBeliefSet;

/**
 * Abstract modal reasoner to be implemented by concrete reasoners. 
 *
 * @author Bastian Wolf
 * @author Nils Geilen
 * @author Anna Gessler
 * @author Matthias Thimm
 *
 */
public abstract class AbstractModalReasoner implements QualitativeReasoner<ModalBeliefSet,FolFormula> {
	
	/**
	 * Empty default prover
	 */
	public static AbstractModalReasoner defaultReasoner = null;
	
	/**
	 * Set default modal reasoner with given
	 * @param reasoner
	 */
	public static void setDefaultReasoner(AbstractModalReasoner reasoner){
		AbstractModalReasoner.defaultReasoner = reasoner;
	}
	
	/**
	 * Returns the default reasoner for modal logic
	 * @return the default modal reasoner
	 */
	public static AbstractModalReasoner getDefaultReasoner(){
		if(AbstractModalReasoner.defaultReasoner != null){
			return AbstractModalReasoner.defaultReasoner;
		} else{
			System.err.println("No default modal reasoner configured, using "
					+ "'NaiveModalReasoner' with default settings as fallback.");			
			return new NaiveModalReasoner();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public abstract Boolean query(ModalBeliefSet beliefbase, FolFormula formula);
}
