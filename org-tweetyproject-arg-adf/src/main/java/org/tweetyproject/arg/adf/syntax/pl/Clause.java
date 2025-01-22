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
package org.tweetyproject.arg.adf.syntax.pl;

import java.util.Collection;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause0;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause1;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause2;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause3;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.ClauseN;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.ExtendedClause;

/**
 * The {@code Clause} interface represents a logical clause, which is a disjunction (OR) of literals in propositional logic.
 * Clauses are fundamental components in SAT solvers and are used to encode constraints and logical formulas.
 *
 * @author Mathias Hofer
 */
public interface Clause extends Iterable<Literal> {

    /**
     * Returns a stream of literals contained in this clause.
     *
     * @return a {@code Stream} of literals in the clause
     */
    Stream<Literal> stream();

    /**
     * Returns the number of literals in this clause.
     *
     * @return the size of the clause
     */
    int size();

    /**
     * Creates a clause with no literals. This represents an empty clause.
     *
     * @return a clause with no literals
     */
    static Clause of() {
        return Clause0.INSTANCE;
    }

    /**
     * Creates a clause with a single literal.
     *
     * @param l the single literal
     * @return a clause containing the given literal
     */
    static Clause of(Literal l) {
        return new Clause1(l);
    }

    /**
     * Creates a clause with two literals.
     *
     * @param l1 the first literal
     * @param l2 the second literal
     * @return a clause containing the given literals
     */
    static Clause of(Literal l1, Literal l2) {
        return new Clause2(l1, l2);
    }

    /**
     * Creates a clause with three literals.
     *
     * @param l1 the first literal
     * @param l2 the second literal
     * @param l3 the third literal
     * @return a clause containing the given literals
     */
    static Clause of(Literal l1, Literal l2, Literal l3) {
        return new Clause3(l1, l2, l3);
    }

    /**
     * Creates a clause by extending an existing clause with an additional literal.
     *
     * @param c the existing clause
     * @param l the additional literal
     * @return a new clause that is the extension of the given clause with the additional literal
     */
    static Clause of(Clause c, Literal l) {
        return new ExtendedClause(c, l);
    }

    /**
     * Creates a clause from a collection of literals.
     *
     * @param literals a collection of literals
     * @return a clause containing the literals from the collection
     */
    static Clause of(Collection<? extends Literal> literals) {
        return new ClauseN(literals.toArray(new Literal[0]));
    }

    /**
     * Creates a clause from a collection of literals and an additional literal.
     *
     * @param literals a collection of literals
     * @param l the additional literal
     * @return a new clause containing the literals from the collection plus the additional literal
     */
    static Clause of(Collection<? extends Literal> literals, Literal l) {
        Literal[] array = literals.toArray(new Literal[literals.size() + 1]);
        array[array.length - 1] = l;
        return new ClauseN(array);
    }

    /**
     * Creates a clause from a collection of literals and two additional literals.
     *
     * @param literals a collection of literals
     * @param l1 the first additional literal
     * @param l2 the second additional literal
     * @return a new clause containing the literals from the collection plus the additional literals
     */
    static Clause of(Collection<? extends Literal> literals, Literal l1, Literal l2) {
        Literal[] array = literals.toArray(new Literal[literals.size() + 2]);
        array[array.length - 2] = l1;
        array[array.length - 1] = l2;
        return new ClauseN(array);
    }

    /**
     * Creates a clause from a collection of literals and three additional literals.
     *
     * @param literals a collection of literals
     * @param l1 the first additional literal
     * @param l2 the second additional literal
     * @param l3 the third additional literal
     * @return a new clause containing the literals from the collection plus the additional literals
     */
    static Clause of(Collection<? extends Literal> literals, Literal l1, Literal l2, Literal l3) {
        Literal[] array = literals.toArray(new Literal[literals.size() + 3]);
        array[array.length - 3] = l1;
        array[array.length - 2] = l2;
        array[array.length - 1] = l3;
        return new ClauseN(array);
    }
}
