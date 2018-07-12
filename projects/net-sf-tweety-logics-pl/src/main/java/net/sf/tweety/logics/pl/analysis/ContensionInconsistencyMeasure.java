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
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class implements the contension inconsistency measure, cf. [Grant, Hunter, 2011].<br/>
 * This measure is defined on paraconsistent models (three-valued models with truth values F,T,B) 
 * of a knowledge base via taking the minimal number of B-valued propositions needed in a model
 * of the knowledge base.<br/>
 * The computation of the inconsistency value is done as follows. First, the knowledge base
 * is transformed into CNF. If the knowledge base is consistent the inconsistency value is 0.
 * Otherwise, for every proposition "p" we create a new knowledge base by removing all clauses
 * where "p" appears (either positive or negative) and test the new knowledge base for consistency.
 * This is equivalent to checking whether there is a paraconsistent model that sets the truth value
 * of p to B. If one of these knowledge bases is consistent we return "1" as inconsistency value. Otherwise,
 * the process is repeated with all pairs "p1,p2" of propositions, then triples, etc.  
 * @author Matthias Thimm
 */
public class ContensionInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PropositionalFormula>{


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PropositionalFormula> formulas) {
		PlBeliefSet bs = new PlBeliefSet(formulas);
		Conjunction cnf = bs.toCnf();
		SubsetIterator<Proposition> it = new IncreasingSubsetIterator<Proposition>(new HashSet<Proposition>((PropositionalSignature)bs.getSignature()));
		while(it.hasNext()){
			Collection<Proposition> props = it.next();
			Conjunction newCnf = new Conjunction(cnf);
			for(Proposition p: props){
				for(PropositionalFormula f: cnf)
					if(f.getAtoms().contains(p))
						newCnf.remove(f);
			}
			if(SatSolver.getDefaultSolver().isConsistent((PropositionalFormula)newCnf))
				return new Double(props.size());
		}
		// this should not happen as at least the paraconsistent interpretation which assigns
		// to every proposition the truth value B is a model
		throw new RuntimeException("Unforeseen exception in computing the contension inconsistency measure: no paraconsistent model found.");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "contension";
	}

}
