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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.commons.QualitativeReasoner;

/**
 * Ancestor class for all adf reasoner
 * 
 * TODO: may be replaced with an interface with default implementations
 * 
 * @author Mathias Hofer
 */
public abstract class AbstractDialecticalFrameworkReasoner
		implements QualitativeReasoner<AbstractDialecticalFramework, Argument>,
		ModelProvider<Argument, AbstractDialecticalFramework, Interpretation> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.dung.reasoner.AbstractDungReasoner#query(net.sf.tweety.
	 * arg.dung.syntax.DungTheory, net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public Boolean query(AbstractDialecticalFramework beliefbase, Argument formula) {
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

	/**
	 * Queries the given AAF for the given argument using the given inference
	 * type.
	 * 
	 * @param beliefbase
	 *            an AAF
	 * @param formula
	 *            a single argument
	 * @param InferenceMode
	 *            either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
	 * @return "true" if the argument is accepted
	 */
	public Boolean query(AbstractDialecticalFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
		Collection<Interpretation> extensions = this.getModels(beliefbase);
		//TODO replace naive implementation
		if (inferenceMode.equals(InferenceMode.SKEPTICAL)) {
			for (Interpretation e : extensions)
				if (!e.satisfies(formula))
					return false;
			return true;
		}
		// so its credulous semantics
		for (Interpretation e : extensions) {
			if (e.satisfies(formula))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.
	 * BeliefBase)
	 */
	@Override
	public abstract Collection<Interpretation> getModels(AbstractDialecticalFramework bbase);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.
	 * BeliefBase)
	 */
	@Override
	public abstract Interpretation getModel(AbstractDialecticalFramework bbase);
}
