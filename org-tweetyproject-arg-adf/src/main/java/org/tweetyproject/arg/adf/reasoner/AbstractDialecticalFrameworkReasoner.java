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
package org.tweetyproject.arg.adf.reasoner;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.reasoner.query.Query;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Ancestor class for all adf reasoner
 * 
 * @deprecated use {@link AbstractDialecticalFramework#query()} instead
 */
@Deprecated( forRemoval = true, since = "1.19" )
public abstract class AbstractDialecticalFrameworkReasoner {
	
	private final IncrementalSatSolver solver;

	/**
	 * @param solver
	 */
	public AbstractDialecticalFrameworkReasoner(IncrementalSatSolver solver) {
		this.solver = Objects.requireNonNull(solver);
	}

	public boolean skepticalQuery(AbstractDialecticalFramework adf, Argument argument) {
		Iterator<Interpretation> iterator = modelIterator(adf);
		while (iterator.hasNext()) {
			Interpretation interpretation = iterator.next();
			if (!interpretation.satisfied(argument)) {
				return false;
			}
		}
		return true;
	}

	public boolean credulousQuery(AbstractDialecticalFramework adf, Argument argument) {
		Iterator<Interpretation> iterator = modelIterator(adf);
		while (iterator.hasNext()) {
			Interpretation interpretation = iterator.next();
			if (interpretation.satisfied(argument)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		while (modelIterator.hasNext()) {
			models.add(modelIterator.next());
		}
		return models;
	}

	public Interpretation getModel(AbstractDialecticalFramework adf) {
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		if (modelIterator.hasNext()) {
			return modelIterator.next();
		}
		return null;
	}

	public Iterator<Interpretation> modelIterator(AbstractDialecticalFramework adf) {
		return query(adf)
				.configure(Configuration.builder().setSatSolver(solver).build())
				.execute()
				.iterator();
	}
	
	abstract Query<Stream<Interpretation>> query(AbstractDialecticalFramework adf);
}