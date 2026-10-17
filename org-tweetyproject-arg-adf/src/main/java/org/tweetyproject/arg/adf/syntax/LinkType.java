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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax;

/**
 * The type of a link (b, a) of an ADF, i.e. how the acceptance condition of a
 * depends on its parent b.
 * <p>
 * The type is not stored in the framework; it is computed on demand by
 * {@link org.tweetyproject.arg.adf.analysis.LinkTypeClassifier}.
 *
 * @author Lars Bengel
 */
public enum LinkType {

	/** Accepting b never turns the condition of a from satisfied to unsatisfied, and sometimes from unsatisfied to satisfied. */
	SUPPORTING,
	/** Accepting b never turns the condition of a from unsatisfied to satisfied, and sometimes from satisfied to unsatisfied. */
	ATTACKING,
	/** The link is neither supporting nor attacking: b helps a in some contexts and hurts it in others. */
	DEPENDENT,
	/** The condition of a does not depend on b at all, although b occurs in it. */
	REDUNDANT;

	/**
	 * @return true if the link is supporting or redundant, i.e. the condition is monotone in the parent
	 */
	public boolean isSupporting() {
		return this == SUPPORTING || this == REDUNDANT;
	}

	/**
	 * @return true if the link is attacking or redundant, i.e. the condition is antitone in the parent
	 */
	public boolean isAttacking() {
		return this == ATTACKING || this == REDUNDANT;
	}

	/**
	 * @return true if the link is not dependent
	 */
	public boolean isBipolar() {
		return this != DEPENDENT;
	}

	/**
	 * Combines the results of the two monotonicity checks of a link.
	 *
	 * @param monotone true if the condition is monotone in the parent
	 * @param antitone true if the condition is antitone in the parent
	 * @return the link type
	 */
	public static LinkType of(boolean monotone, boolean antitone) {
		if (monotone && antitone) {
			return REDUNDANT;
		} else if (monotone) {
			return SUPPORTING;
		} else if (antitone) {
			return ATTACKING;
		}
		return DEPENDENT;
	}

}
