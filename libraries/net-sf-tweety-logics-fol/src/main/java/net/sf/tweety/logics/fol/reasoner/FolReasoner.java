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
package net.sf.tweety.logics.fol.reasoner;

import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Abstract FOL Prover to be implemented by concrete solvers
 * @author Bastian Wolf
 * @author Nils Geilen
 * @author Matthias Thimm
 *
 */
public abstract class FolReasoner implements QualitativeReasoner<FolBeliefSet,FolFormula> {

	/**
	 * Empty default reasoner
	 */
	public static FolReasoner defaultReasoner = null;
	
	/**
	 * Set default reasoner with given
	 * @param reasoner
	 */
	public static void setDefaultReasoner(FolReasoner reasoner){
		FolReasoner.defaultReasoner = reasoner;
	}
	
	/**
	 * Returns the default theorem prover
	 * @return the default theorem prover
	 */
	public static FolReasoner getDefaultReasoner(){
		if(FolReasoner.defaultReasoner != null){
			return FolReasoner.defaultReasoner;
		} else{
			System.err.println("No default FOL reasoner configured, using "
					+ "'NaiveReasoner' with default settings as fallback. "
					+ "It is strongly advised that a default FOL Reasoner is manually configured, see "
					+ "'http://tweetyproject.org/doc/fol-provers.html' "
					+ "for more information.");			
			return new SimpleFolReasoner();
		}
	}
	
	/**
	 * This method determines whether two formulas are
	 * equivalent wrt. to the given knowledge base.
	 * @param kb the knowledge base
	 * @param a  the first formula.
	 * @param b  the second formula.
	 * @return   the answer to the query.
	 */
	public abstract boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public abstract Boolean query(FolBeliefSet beliefbase, FolFormula formula);	
}
