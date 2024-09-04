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
package org.tweetyproject.arg.adf.reasoner.heuristics;

import java.util.Comparator;
import java.util.Objects;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * A comparator for comparing arguments in an Abstract Dialectical Framework (ADF) based on their degree.
 * The degree can be based on incoming, outgoing, or undirected links in the framework.
 * This class is deprecated and should not be used anymore.
 *
 * @author Mathias Hofer
 * @deprecated This class is no longer used and will be removed in a future version.
 */
@Deprecated(forRemoval = true)
public final class DegreeComparator implements Comparator<Argument> {

    /**
     * An enumeration for the type of degree to be used in comparisons.
     * The degree type specifies whether the comparison is based on:
     * <ul>
     *   <li>{@link #INCOMING}: the number of incoming links</li>
     *   <li>{@link #OUTGOING}: the number of outgoing links</li>
     *   <li>{@link #UNDIRECTED}: the sum of both incoming and outgoing links</li>
     * </ul>
     *
     * @author Mathias Hofer
     */
    public static enum DegreeType {
        /** Compare based on the number of incoming links. */
        INCOMING,

        /** Compare based on the number of outgoing links. */
        OUTGOING,

        /** Compare based on the sum of both incoming and outgoing links. */
        UNDIRECTED
    }

    /** The type of degree to be used in the comparison. */
    private final DegreeType degreeType;

    /** The Abstract Dialectical Framework (ADF) in which the arguments exist. */
    private final AbstractDialecticalFramework adf;

    /**
     * Constructs a new {@code DegreeComparator} with the specified ADF and degree type.
     *
     * @param adf the Abstract Dialectical Framework (ADF) to be used for calculating degrees
     * @param degreeType the type of degree to be used for comparison (e.g., INCOMING, OUTGOING, or UNDIRECTED)
     * @throws NullPointerException if either {@code adf} or {@code degreeType} is {@code null}
     */
    public DegreeComparator(AbstractDialecticalFramework adf, DegreeType degreeType) {
        this.adf = Objects.requireNonNull(adf);
        this.degreeType = Objects.requireNonNull(degreeType);
    }

    /**
     * Compares two arguments in the ADF based on their degree.
     * The degree type (incoming, outgoing, or undirected) is specified at the time of the comparator's construction.
     *
     * @param a1 the first argument to compare
     * @param a2 the second argument to compare
     * @return a negative integer, zero, or a positive integer as the degree of {@code a1} is less than,
     *         equal to, or greater than the degree of {@code a2}
     */
    @Override
    public int compare(Argument a1, Argument a2) {
        int degree1 = degree(a1, adf);
        int degree2 = degree(a2, adf);
        return Integer.compare(degree1, degree2);
    }

    /**
     * Calculates the degree of the specified argument based on the degree type.
     *
     * @param arg the argument whose degree is to be calculated
     * @param adf the Abstract Dialectical Framework (ADF) in which the argument exists
     * @return the degree of the argument based on the degree type (INCOMING, OUTGOING, or UNDIRECTED)
     */
    private int degree(Argument arg, AbstractDialecticalFramework adf) {
        switch (degreeType) {
            case INCOMING:
                return adf.incomingDegree(arg);
            case OUTGOING:
                return adf.outgoingDegree(arg);
            case UNDIRECTED:
                return adf.incomingDegree(arg) + adf.outgoingDegree(arg);
            default:
                throw new AssertionError("Unknown degree type: " + degreeType);
        }
    }
}

