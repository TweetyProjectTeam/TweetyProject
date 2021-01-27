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
package org.tweetyproject.arg.adf.syntax.acc;

import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * A general and easy-to-use interface which allows for type-safe operations on the {@link AcceptanceCondition} hierarchy.
 * 
 * @author Mathias Hofer
 *
 * @param <U> the bottom-up information
 * @param <D> the top-down information
 */
public interface Visitor<U, D> {

	U visit(TautologyAcceptanceCondition acc, D topDownData);

	U visit(ContradictionAcceptanceCondition acc, D topDownData);

	U visit(ConjunctionAcceptanceCondition acc, D topDownData);

	U visit(DisjunctionAcceptanceCondition acc, D topDownData);

	U visit(EquivalenceAcceptanceCondition acc, D topDownData);

	U visit(ExclusiveDisjunctionAcceptanceCondition acc, D topDownData);

	U visit(ImplicationAcceptanceCondition acc, D topDownData);

	U visit(NegationAcceptanceCondition acc, D topDownData);

	U visit(Argument acc, D topDownData);

}
