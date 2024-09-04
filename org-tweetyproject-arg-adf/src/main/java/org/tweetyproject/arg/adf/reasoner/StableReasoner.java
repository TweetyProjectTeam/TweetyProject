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
 * This class represents a reasoner for determining stable interpretations
 * in an Abstract Dialectical Framework (ADF). The reasoner uses a SAT solver
 * to compute stable interpretations.
 *
 * <p>Note: This class is deprecated and scheduled for removal in version 1.19.
 * It is recommended to use an updated reasoner implementation instead.</p>
 *
 * @deprecated This class is deprecated and scheduled for removal since version 1.19.
 *             Use alternative reasoners for stable interpretations in ADFs.
 *
 * @author Mathias Hofer
 * @since 1.19
 */
@Deprecated(forRemoval = true, since = "1.19")
public class StableReasoner extends AbstractDialecticalFrameworkReasoner {

    /**
     * Constructs a new StableReasoner using the given incremental SAT solver.
     *
     * @param solver the underlying incremental SAT solver used for querying
     *               stable interpretations in the Abstract Dialectical Framework.
     */
    public StableReasoner(IncrementalSatSolver solver) {
        super(solver);
    }

    /**
     * Queries the Abstract Dialectical Framework (ADF) for stable interpretations.
     *
     * @param adf the Abstract Dialectical Framework to query
     * @return a query returning a stream of stable interpretations from the ADF
     */
    @Override
    Query<Stream<Interpretation>> query(AbstractDialecticalFramework adf) {
        return adf.query().stable().interpretations();
    }
}

