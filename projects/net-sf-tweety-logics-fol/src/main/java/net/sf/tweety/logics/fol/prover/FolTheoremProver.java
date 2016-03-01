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

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * 
 * @author Bastian Wolf, Nils Geilen
 *
 */
public abstract class FolTheoremProver {

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
			// TODO return standard prover?
			System.err.println("No default E Prover set! Returning null...");
			return null;
		}
	}
	
	/**
	 * This method determines the answer of the given query
	 * wrt. to the given knowledge base.
	 * @param kb th eknowedge base
	 * @param query a query.
	 * @return the answer to the query.
	 */
	public abstract boolean query(FolBeliefSet kb, FolFormula query);
	
}
