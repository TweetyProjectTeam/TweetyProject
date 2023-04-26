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

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an comparator, which defines if 2 frameworks are equivalent, 
 * by comparing their serialisable extensions. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByExtension extends SerialisationEquivalence<Collection<Extension<DungTheory>>> {
	
	private SerialisableExtensionReasoner reasoner;
	
	/**
	 * @param comparator {@link SerialisationEquivalence::comparator}
	 * @param reasoner Reasoner used to compute the extensions
	 */
	public SerialisationEquivalenceByExtension(IEquivalence<Collection<Extension<DungTheory>>> comparator,
			SerialisableExtensionReasoner reasoner) {
		super(comparator);
		this.reasoner = reasoner;
	}

	@Override
	protected Collection<Extension<DungTheory>> getRelevantAspect(DungTheory framework) {
		return reasoner.getModels(framework);
	}

	@Override
	protected DungTheory getFramework(Collection<Extension<DungTheory>> object) {
		// not supported
		return null;
	}
	
	
}
