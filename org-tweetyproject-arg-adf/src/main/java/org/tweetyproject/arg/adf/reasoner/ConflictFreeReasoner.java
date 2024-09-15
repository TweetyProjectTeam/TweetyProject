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

import java.util.stream.Stream;

import org.tweetyproject.arg.adf.reasoner.query.Query;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * This class implements a reasoner for conflict-free semantics in Abstract Dialectical Frameworks (ADFs).
 * It uses a SAT solver to compute conflict-free interpretations.
 * <p>
 * This class is deprecated and is scheduled for removal in future versions.
 * Users are advised to switch to updated reasoner classes for future-proof implementations.
 * </p>
 *
 * @author Mathias Hofer
 * @deprecated since 1.19, for removal in future versions
 */
@Deprecated(forRemoval = true, since = "1.19")
public class ConflictFreeReasoner extends AbstractDialecticalFrameworkReasoner {

    /**
     * Constructs a ConflictFreeReasoner with the given SAT solver.
     *
     * @param solver the underlying SAT solver used to compute conflict-free interpretations
     */
    public ConflictFreeReasoner(IncrementalSatSolver solver) {
        super(solver);
    }

    /**
     * Queries the given Abstract Dialectical Framework (ADF) for conflict-free interpretations.
     * This method retrieves the query that computes the conflict-free interpretations for the ADF.
     *
     * @param adf the Abstract Dialectical Framework to query for conflict-free interpretations
     * @return a query that streams the conflict-free interpretations of the ADF
     */
    @Override
    Query<Stream<Interpretation>> query(AbstractDialecticalFramework adf) {
        return adf.query().conflictFree().interpretations();
    }
}

