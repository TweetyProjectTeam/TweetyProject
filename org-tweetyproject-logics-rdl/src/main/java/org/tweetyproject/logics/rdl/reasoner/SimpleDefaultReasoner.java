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
package org.tweetyproject.logics.rdl.reasoner;

import java.util.Collection;

import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.rdl.semantics.DefaultProcessTree;
import org.tweetyproject.logics.rdl.semantics.Extension;
import org.tweetyproject.logics.rdl.syntax.DefaultTheory;

/**
 * Implements a naive reasoner for default logic based on exhaustive 
 * application of defaults in process trees.
 * 
 * @author Matthias Thimm, Nils Geilen
 */
public class SimpleDefaultReasoner implements QualitativeReasoner<DefaultTheory,FolFormula>, ModelProvider<FolFormula,DefaultTheory,Extension>{
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<Extension> getModels(DefaultTheory bbase) {
		return new DefaultProcessTree(bbase).getExtensions();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Extension getModel(DefaultTheory bbase) {
		// just return the first one
		return this.getModels(bbase).iterator().next();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Boolean query(DefaultTheory theory, FolFormula query) {
		return this.query(theory, query, InferenceMode.SKEPTICAL);
	}
	
	/**
	 * Queries the given default theory for the given query using the given 
	 * inference mode.
	 * @param theory a default theory
	 * @param query a formula
	 * @param inferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
	 * @return "true" if the formula is accepted
	 */
	public Boolean query(DefaultTheory theory, FolFormula query, InferenceMode inferenceMode) {
		if(!query.isGround())
			throw new IllegalArgumentException("Query is not grounded.");
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)) {
			for (Extension extension: this.getModels(theory)){			
				if(!extension.satisfies(query))
					return false;
			}
			return true;
		}
		for (Extension extension: this.getModels(theory)){			
			if(extension.satisfies(query))
				return true;
		}
		return false;		
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
	
}
