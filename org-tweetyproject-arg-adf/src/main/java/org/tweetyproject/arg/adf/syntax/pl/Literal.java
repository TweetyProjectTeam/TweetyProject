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

import org.tweetyproject.arg.adf.syntax.pl.Literals.NamedAtom;
import org.tweetyproject.arg.adf.syntax.pl.Literals.TransientAtom;
import org.tweetyproject.arg.adf.syntax.pl.Literals.UnnamedAtom;

/**
 * The {@code Literal} interface represents a logical literal, which can be either a positive or negative
 * proposition. Literals are fundamental components in propositional logic and SAT solvers. They can be
 * used to construct clauses, represent assumptions, and encode constraints.
 * 
 *
 * @author Mathias Hofer
 */
public interface Literal {

    /**
     * Checks if this literal is transient. Transient literals are usually temporary and are not
     * used as permanent identifiers.
     *
     * @return {@code true} if the literal is transient, {@code false} otherwise
     */
    boolean isTransient();

    /**
     * Checks if this literal is positive. A positive literal represents a proposition in its
     * original form, whereas a negative literal represents the negation of a proposition.
     *
     * @return {@code true} if the literal is positive, {@code false} if it is negative
     */
    boolean isPositive();

    /**
     * Retrieves the atom of this literal. If the literal is a negation, this method returns the
     * encapsulated atom. If the literal is an atom, it returns itself.
     *
     * @return the atom of the literal, or this literal if it is an atom
     */
    Literal getAtom();

    /**
     * Returns the name of this literal. The name may be {@code null} if the literal does not have
     * a specific name (e.g., in the case of transient literals).
     *
     * @return the name of the literal, or {@code null} if it does not have a name
     */
    String getName();

    /**
     * Returns the negation of this literal. The following properties hold for every literal {@code l}:
     * <ul>
     * <li>{@code l.neg().neg() == l}: The negation of the negation returns the original literal.</li>
     * <li>{@code l.neg().getAtom() == l.getAtom()}: The negation of the literal has the same atom.</li>
     * </ul>
     * The following property might not hold for every literal {@code l}:
     * <ul>
     * <li>{@code l.neg() == l.neg()}: The negation of a literal might not be equal to itself.</li>
     * </ul>
     *
     * @return the negation of this literal
     */
    Literal neg();

    /**
     * Creates a new unnamed atom literal. This is a default literal without any specific name.
     *
     * @return a new unnamed atom literal
     */
    static Literal create() {
        return new UnnamedAtom();
    }

    /**
     * Creates a new named atom literal with the specified name. If the provided name is {@code null},
     * an unnamed atom literal is created instead.
     *
     * @param name the name of the literal, or {@code null} for an unnamed literal
     * @return a new named atom literal, or an unnamed atom literal if the name is {@code null}
     */
    static Literal create(String name) {
        if (name == null) {
            return new UnnamedAtom();
        }
        return new NamedAtom(name);
    }

    /**
     * Creates a new transient atom literal. Transient literals are typically used for temporary or
     * intermediate purposes and do not have a permanent name.
     *
     * @return a new transient atom literal
     */
    static Literal createTransient() {
        return new TransientAtom();
    }
}
