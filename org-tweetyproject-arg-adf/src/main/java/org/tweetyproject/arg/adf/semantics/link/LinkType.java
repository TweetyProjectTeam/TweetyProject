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
package org.tweetyproject.arg.adf.semantics.link;
/**
 * Enum representing different types of links in a framework. A link can be
 * dependent, supporting, attacking, or redundant. Each link type has certain
 * properties that define whether it is attacking, supporting, or both.
 *
 * @author Sebastian
 */
public enum LinkType {
    /** Dependent link: neither attacking nor supporting */
    DEPENDENT(false, false),
    /** Supporting link: link provides support */
    SUPPORTING(false, true),
    /** Attacking link: link represents an attack */
    ATTACKING(true, false),
    /** Redundant link: both attacking and supporting */
    REDUNDANT(true, true);

    private final boolean attacking;
    private final boolean supporting;

    /**
     * Constructs a LinkType with specified properties of whether it is attacking
     * and/or supporting.
     *
     * @param attacking   whether the link is attacking
     * @param supporting  whether the link is supporting
     */
    private LinkType(boolean attacking, boolean supporting) {
        this.attacking = attacking;
        this.supporting = supporting;
    }

    /**
     * Checks if the link is attacking.
     *
     * @return true if the link is attacking, otherwise false
     */
    public boolean isAttacking() {
        return attacking;
    }

    /**
     * Checks if the link is supporting.
     *
     * @return true if the link is supporting, otherwise false
     */
    public boolean isSupporting() {
        return supporting;
    }

    /**
     * Checks if the link is redundant, meaning it is both attacking and supporting.
     *
     * @return true if the link is redundant, otherwise false
     */
    public boolean isRedundant() {
        return attacking && supporting;
    }

    /**
     * Checks if the link is dependent, meaning it is neither attacking nor supporting.
     *
     * @return true if the link is dependent, otherwise false
     */
    public boolean isDependent() {
        return !attacking && !supporting;
    }

    /**
     * Checks if the link is bipolar, meaning it is either attacking or supporting.
     *
     * @return true if the link is attacking or supporting, otherwise false
     */
    public boolean isBipolar() {
        return attacking || supporting;
    }

    /**
     * Checks if the link is non-bipolar, meaning it is neither attacking nor supporting.
     *
     * @return true if the link is non-bipolar, otherwise false
     */
    public boolean isNonBipolar() {
        return !isBipolar();
    }

    /**
     * Returns the corresponding LinkType based on the given properties of
     * attacking and supporting.
     *
     * @param attacking   whether the link is attacking
     * @param supporting  whether the link is supporting
     * @return the appropriate LinkType based on the properties
     */
    public static LinkType get(boolean attacking, boolean supporting) {
        if (attacking && supporting) {
            return REDUNDANT;
        } else if (attacking) {
            return ATTACKING;
        } else if (supporting) {
            return SUPPORTING;
        } else {
            return DEPENDENT;
        }
    }
}

