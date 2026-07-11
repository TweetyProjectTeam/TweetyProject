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
 * Ancestor class for ADF reasoners backed by an incremental SAT solver.
 *
 * @deprecated use {@link AbstractDialecticalFramework#query()} instead
 */
@Deprecated( forRemoval = true, since = "1.19" )
public abstract class AbstractDialecticalFrameworkReasoner extends AbstractADFReasoner {
		
	/** The SAT solver used for model enumeration. */
	private final IncrementalSatSolver solver;

	/**
	 * Creates a new abstract dialectical framework reasoner.
	 *
	 * @param solver the SAT solver used for model enumeration
	 */
	public AbstractDialecticalFrameworkReasoner(IncrementalSatSolver solver) {
		this.solver = Objects.requireNonNull(solver);
	}

	/**
	 * Checks whether the given argument is accepted under skeptical semantics.
	 *
	 * @param adf the ADF to query
	 * @param argument the argument to check
	 * @return {@code true} iff all models satisfy the argument
	 */
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

	/**
	 * Checks whether the given argument is accepted under credulous semantics.
	 *
	 * @param adf the ADF to query
	 * @param argument the argument to check
	 * @return {@code true} iff at least one model satisfies the argument
	 */
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

	/**
	 * Returns all models of the given ADF.
	 *
	 * @param adf the ADF to query
	 * @return all models of the ADF
	 */
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		while (modelIterator.hasNext()) {
			models.add(modelIterator.next());
		}
		return models;
	}

	/**
	 * Returns one model of the given ADF, if available.
	 *
	 * @param adf the ADF to query
	 * @return one model of the ADF or {@code null} if none exist
	 */
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		if (modelIterator.hasNext()) {
			return modelIterator.next();
		}
		return null;
	}

	/**
	 * Returns an iterator over all models of the given ADF.
	 *
	 * @param adf the ADF to query
	 * @return an iterator over all models
	 */
	public Iterator<Interpretation> modelIterator(AbstractDialecticalFramework adf) {
		return query(adf)
				.configure(Configuration.builder().setSatSolver(solver).build())
				.execute()
				.iterator();
	}
	
	/**
	 * Creates the query used to enumerate models.
	 *
	 * @param adf the ADF to query
	 * @return the query producing all models
	 */
	abstract Query<Stream<Interpretation>> query(AbstractDialecticalFramework adf);
}
