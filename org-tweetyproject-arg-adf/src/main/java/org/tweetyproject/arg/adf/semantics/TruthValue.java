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
package org.tweetyproject.arg.adf.semantics;

/**
 * The truth values of three-valued interpretations, with the connectives of
 * Kleene's strong three-valued logic and the information order ≤_i, in which
 * u ≤_i t and u ≤_i f.
 *
 * @author Lars Bengel
 */
public enum TruthValue {

	/** true (t) */
	TRUE,
	/** false (f) */
	FALSE,
	/** undecided or unknown (u) */
	UNDECIDED;

	/**
	 * @return the Kleene negation: t and f are swapped, u stays u
	 */
	public TruthValue not() {
		return switch (this) {
			case TRUE -> FALSE;
			case FALSE -> TRUE;
			case UNDECIDED -> UNDECIDED;
		};
	}

	/**
	 * @param other a truth value
	 * @return the Kleene conjunction: f if one is f, t if both are t, u otherwise
	 */
	public TruthValue and(TruthValue other) {
		if (this == FALSE || other == FALSE) {
			return FALSE;
		}
		return this == TRUE && other == TRUE ? TRUE : UNDECIDED;
	}

	/**
	 * @param other a truth value
	 * @return the Kleene disjunction: t if one is t, f if both are f, u otherwise
	 */
	public TruthValue or(TruthValue other) {
		if (this == TRUE || other == TRUE) {
			return TRUE;
		}
		return this == FALSE && other == FALSE ? FALSE : UNDECIDED;
	}

	/**
	 * @param other a truth value
	 * @return true if this value carries at most as much information as the other
	 */
	public boolean leqInfo(TruthValue other) {
		return this == UNDECIDED || this == other;
	}

	/**
	 * @param other a truth value
	 * @return the consensus (meet w.r.t. ≤_i): the common value if both agree, u otherwise
	 */
	public TruthValue consensus(TruthValue other) {
		return this == other ? this : UNDECIDED;
	}

	/**
	 * @return true for t and f
	 */
	public boolean isDecided() {
		return this != UNDECIDED;
	}

	/**
	 * @param value a classical truth value
	 * @return t for true and f for false
	 */
	public static TruthValue valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * @return "t", "f" or "u"
	 */
	@Override
	public String toString() {
		return switch (this) {
			case TRUE -> "t";
			case FALSE -> "f";
			case UNDECIDED -> "u";
		};
	}

}
