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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 package org.tweetyproject.arg.delp.semantics;

/**
 * The `DelpAnswer` class represents a wrapper around a generic answer from a reasoner,
 * allowing for an additional state of `UNDECIDED` alongside the traditional `YES` and `NO`.
 *
 * <p>
 * This class ensures backward compatibility by mapping these three states to `Double` values
 * as follows:
 * </p>
 * <ul>
 *     <li><code>true  &lt;=&gt; YES       &lt;=&gt; Double(0)</code></li>
 *     <li><code>false &lt;=&gt; NO        &lt;=&gt; negative number</code></li>
 *     <li><code>false &lt;=&gt; UNDECIDED &lt;=&gt; positive number</code></li>
 * </ul>
 * <p>
 * Note that only <code>true</code> can be reliably mapped to `YES`, while <code>false</code>
 * remains ambiguous and is mapped by default to `NO`.
 * </p>
 *
 * <p><b>Authors:</b> Linda Briesemeister</p>
 */
public class DelpAnswer {

    /**
     * The `Type` enum represents the possible types of answers: `YES`, `NO`, `UNDECIDED`, and `UNKNOWN`.
     * Each type has an associated textual description.
     */
    public enum Type {
        /** Represents an affirmative answer. */
        YES ("The answer is: YES"),

        /** Represents a negative answer. */
        NO ("The answer is: NO"),

        /** Represents an undecided answer, indicating that the reasoner cannot determine a clear yes or no. */
        UNDECIDED ("The answer is: UNDECIDED"),

        /** Represents an unknown answer, typically used when the state of the answer is indeterminate or unclassified. */
        UNKNOWN ("The answer is: UNKNOWN");

        /** The textual representation of the answer type. */
        private final String text;

        /**
         * Constructor for the `Type` enum, associating a text description with the type.
         *
         * @param text the text description of the type.
         */
        Type(String text) { this.text = text; }

        /**
         * Maps a boolean value to a `Type`. `true` is mapped to `YES`, while `false` is mapped to `NO`.
         *
         * @param booleanAnswer the boolean value to be mapped.
         * @return the corresponding `Type` (`YES` or `NO`).
         */
        static Type typeForBoolean(boolean booleanAnswer) {
            if (booleanAnswer)
                return YES;
            else
                return NO; // ambiguous, so default is NO
        }

        /**
         * Maps a `Double` value to a `Type`. `0` is mapped to `YES`, negative numbers to `NO`, and positive numbers to `UNDECIDED`.
         *
         * @param doubleAnswer the `Double` value to be mapped.
         * @return the corresponding `Type` (`YES`, `NO`, or `UNDECIDED`).
         */
        static Type typeForDouble(Double doubleAnswer) {
            if (doubleAnswer == 0d)
                return YES;
            else if (doubleAnswer < 0d)
                return NO;
            else // double is positive
                return UNDECIDED;
        }

        /**
         * Returns the textual description of the answer type.
         *
         * @return the textual description of the answer type.
         */
        @Override
        public String toString() { return text; }

        /**
         * Returns the boolean representation of the answer type.
         *
         * @return `true` if the type is `YES`, otherwise `false`.
         */
        public boolean getBooleanAnswer() {
            switch (this) {
                case YES: return true;
                default: return false;
            }
        }

        /**
         * Returns the `Double` representation of the answer type.
         *
         * @return `0d` for `YES`, `-1d` for `NO`, `1d` for `UNDECIDED`, and `Double.NaN` for `UNKNOWN`.
         * @throws IllegalStateException if the type cannot be converted to a `Double`.
         */
        public Double getDoubleAnswer() {
            switch (this) {
                case YES: return 0d;
                case NO: return -1d;
                case UNDECIDED: return 1d;
                case UNKNOWN: return Double.NaN;
                default:
                    throw new IllegalStateException("Cannot generate Double answer from " + this);
            }
        }
    }

    /** Default Constructor */
    public DelpAnswer(){}
}

