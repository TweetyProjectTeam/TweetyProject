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
package org.tweetyproject.arg.dung.analysis;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an inconsistency measure based on the count of preferred extensions in a Dung theory.
 * Preferred count inconsistency measure is the count of preferred extensions of the argumentation framework minus one.
 * It implements the InconsistencyMeasure interface.
 *
 * @author Timothy Gillespie
 * @param <T> the type of Dung theory
 */
public class PreferredCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	/**
 * Calculates the inconsistency measure of the given argumentation framework based on preferred extensions.
 * Preferred count inconsistency measure is computed by determining the count of preferred extensions
 * and subtracting one from it.
 *
 * @param argumentationFramework The argumentation framework for which to calculate the inconsistency measure.
 * @return The preferred count inconsistency measure of the argumentation framework.
 *         Returns the count of preferred extensions minus one.
 *         If there are no preferred extensions, returns -1.0.
 */
	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		Collection<Extension<DungTheory>> preferredExtensions = new SimplePreferredReasoner().getModels(argumentationFramework);
		Double preferredCount = (Double) ((double) preferredExtensions.size());
		return preferredCount - 1.0;
	}



}
