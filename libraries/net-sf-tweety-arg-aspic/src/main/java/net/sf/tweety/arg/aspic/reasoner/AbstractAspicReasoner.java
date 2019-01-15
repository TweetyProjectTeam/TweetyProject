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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.reasoner;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * The abstract ancestor of all Aspic reasoner implementations
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas
 */
public abstract class AbstractAspicReasoner<T extends Invertable> implements QualitativeReasoner<AspicArgumentationTheory<T>,T> {
	
	/**
	 * Underlying reasoner for AAFs. 
	 */
	private AbstractExtensionReasoner aafReasoner;
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public AbstractAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		this.aafReasoner = aafReasoner;
	}
	
	/**
	 * Computes the Dung theory from which the answer will be derived 
	 * @param aat some Aspic theory
	 * @param query some query 
	 * @return a Dung theory
	 */
	public abstract DungTheory getDungTheory(AspicArgumentationTheory<T> aat, T query);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Boolean query(AspicArgumentationTheory<T> aat, T query) {
		return this.query(aat, query, InferenceMode.SKEPTICAL);	
	}
	
	/**
	 * Queries the given AspicArgumentationTheory for the given formula using the given 
	 * inference type.
	 * @param aat an AspicArgumentationTheory
	 * @param query a formula
	 * @param inferenceType either Semantics.SCEPTICAL_INFERENCE or Semantics.CREDULOUS_INFERENCE
	 * @return "true" if the formula is accepted
	 */
	public final Boolean query(AspicArgumentationTheory<T> aat, T query, InferenceMode inferenceMode) {
		return query(getDungTheory(aat, query), query, inferenceMode);
	}

	public final Boolean query(DungTheory dt, T query, InferenceMode inferenceMode) {
		for (Argument arg : dt) 
			if (((AspicArgument<?>)arg).getConclusion().equals(query)) 
				if (this.aafReasoner.query(dt,arg,inferenceMode)) 
					return true;
		return false;	
	}
}
