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
package org.tweetyproject.logics.ml.syntax;

import java.util.Set;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

/**
 * This class models a modal knowledge base, i.e. a set of formulas
 * in modal logic.
 * 
 * @author Anna Gessler
 */
public class MlBeliefSet extends BeliefSet<RelationalFormula,FolSignature> {
	
	/**
	 * Creates a new empty modal knowledge base.
	 */
	public MlBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new modal knowledge base with the given set of formulas.
	 * @param formulas some formulas
	 */
	public MlBeliefSet(Set<RelationalFormula> formulas){
		super(formulas);
	}

	@Override
	public Signature getMinimalSignature() {
		FolSignature sig = new FolSignature();
		for(Formula m: this) {
			while (m instanceof MlFormula) {
				m = ((MlFormula)m).getFormula(); 
			}
			sig.add(m);	
			}	
		return sig;
	}

	@Override
	protected FolSignature instantiateSignature() {
		return new FolSignature();
	}

}
