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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an comparator, which defines if 2 frameworks are equivalent,
 * by comparing their serialisation graphs.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByGraph extends SerialisationEquivalence<SerialisationGraph> {

	private SerialisableExtensionReasoner reasoner;

	/**
	 * @param comparator @see SerialisationEquivalence
	 * @param reasoner Reasoner used to compute the graphs
	 */
	public SerialisationEquivalenceByGraph(Equivalence<SerialisationGraph> comparator,
			SerialisableExtensionReasoner reasoner) {
		super(comparator);
		this.reasoner = reasoner;
	}

	@Override
	protected DungTheory getFramework(SerialisationGraph object) {
		// not supported
		return null;
	}

	@Override
	protected SerialisationGraph getRelevantAspect(DungTheory framework) {
		return this.reasoner.getModelsGraph(framework);
	}

}
