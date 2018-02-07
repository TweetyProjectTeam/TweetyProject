/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.prover;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Abstract FOL Prover to be implemented by concrete solvers
 * @author Bastian Wolf
 * @author Nils Geilen
 *
 */
public abstract class FolTheoremProver extends Reasoner {

	public FolTheoremProver(BeliefBase beliefBase) {
		super(beliefBase);
	}

	/**
	 * Empty default prover
	 */
	public static FolTheoremProver defaultProver = null;
	
	/**
	 * Set default prover with given
	 * @param prover
	 */
	public static void setDefaultProver(FolTheoremProver prover){
		FolTheoremProver.defaultProver = prover;
	}
	
	/**
	 * Returns the default theorem prover
	 * @return the default theorem prover
	 */
	public static FolTheoremProver getDefaultProver(){
		if(FolTheoremProver.defaultProver != null){
			return FolTheoremProver.defaultProver;
		} else{
			System.err.println("No default theorem prover configured, using "
					+ "'NaiveProver' with default settings as fallback. "
					+ "It is strongly advised that a default theorem prover is manually configured, see "
					+ "'http://tweetyproject.org/doc/fol-provers.html' "
					+ "for more information.");			
			return new NaiveProver();
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
	
}
