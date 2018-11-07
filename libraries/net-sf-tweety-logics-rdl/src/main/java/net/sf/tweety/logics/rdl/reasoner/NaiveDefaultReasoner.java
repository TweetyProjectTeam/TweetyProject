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
package net.sf.tweety.logics.rdl.reasoner;

import java.util.Collection;

import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.semantics.DefaultProcessTree;
import net.sf.tweety.logics.rdl.semantics.Extension;
import net.sf.tweety.logics.rdl.syntax.DefaultTheory;

/**
 * Implements a naive reasoner for default logic based on exhaustive 
 * application of defaults in process trees.
 * 
 * @author Matthias Thimm, Nils Geilen
 */
public class NaiveDefaultReasoner implements QualitativeReasoner<DefaultTheory,FolFormula>, ModelProvider<FolFormula,DefaultTheory,Extension>{
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Collection<Extension> getModels(DefaultTheory bbase) {
		return new DefaultProcessTree(bbase).getExtensions();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Extension getModel(DefaultTheory bbase) {
		// just return the first one
		return this.getModels(bbase).iterator().next();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
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
	 * @param InferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
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
	
}
