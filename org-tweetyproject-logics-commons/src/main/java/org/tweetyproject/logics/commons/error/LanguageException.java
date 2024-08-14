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
package org.tweetyproject.logics.commons.error;

/**
 * A {@code LanguageException} is thrown when an illegal operation is attempted
 * in a language, such as setting the arity of a propositional predicate to a value greater than zero.
 *
 * <p>
 * This exception is used to signal various types of errors related to the misuse
 * of a language, particularly when specific features or operations are not supported.
 * </p>
 *
 * <p><b>Authors:</b> Tim Janus</p>
 */
public class LanguageException extends RuntimeException {
    /** Serialization ID to avoid compiler warnings. */
    private static final long serialVersionUID = 649864945437272048L;

    /**
     * Enumeration representing specific reasons why a {@code LanguageException} might be thrown.
     * Each reason corresponds to a particular type of illegal operation or unsupported feature in the language.
     */
    public static enum LanguageExceptionReason {
        /** Tried to generate an illegal predicate. */
        LER_ILLEGAL_PREDICATE("Tried to generate an illegal predicate."),

        /** Tried to instantiate an unsupported term. */
        LER_TERM_TYPE_NOT_SUPPORTED("Tried to instantiate an unsupported term."),

        /** Rules are not supported by the language. */
        LER_RULES_NOT_SUPPORTED("Rules are not supported by the language."),

        /** Disjunctions are not supported by the language. */
        LER_DISJUNCTIONS_NOT_SUPPORTED("Disjunctions are not supported by the language."),

        /** Conjunctions are not supported by the language. */
        LER_CONJUNCTIONS_NOT_SUPPORTED("Conjunctions are not supported by the language."),

        /** Associative formulas are not supported by the language. */
        LER_ASSOCIATIVE_NOT_SUPPORTED("Associative formulas are not supported by the language."),

        /** Quantified formulas are not supported by the language. */
        LER_QUANTIFICATION_NOT_SUPPORTED("Quantified formulas are not supported by the language."),

        /** Dynamic instantiation did not work. */
        LER_INSTANTIATION("Dynamic instantiation did not work."),

        /** Illegal access occurred. */
        LER_ILLEGAL_ACCESSS("Illegal access.");

        /** A human-readable description of the exception reason. */
        private final String name;

        /**
         * Constructs a {@code LanguageExceptionReason} with a specific description.
         *
         * @param name the description of the exception reason.
         */
        private LanguageExceptionReason(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Constructs a {@code LanguageException} with a default error message.
     * This constructor is typically used when the exact nature of the error is unspecified.
     */
    public LanguageException() {
        super("Language used incorrectly");
    }

    /**
     * Constructs a {@code LanguageException} with a specified language and reason.
     *
     * @param language the name of the language in which the error occurred.
     * @param reason   the specific reason for the exception, described by a {@link LanguageExceptionReason}.
     */
    public LanguageException(String language, LanguageExceptionReason reason) {
        this(language, reason, "");
    }

    /**
     * Constructs a {@code LanguageException} with a specified language, reason, and additional information.
     *
     * @param language            the name of the language in which the error occurred.
     * @param reason              the specific reason for the exception, described by a {@link LanguageExceptionReason}.
     * @param furtherInformation  additional details about the error, which may help in diagnosing the issue.
     */
    public LanguageException(String language, LanguageExceptionReason reason, String furtherInformation) {
        super("The language '" + language + "' is used incorrectly: " +
                reason.toString() + (furtherInformation.isEmpty() ? "" : " - " + furtherInformation));
    }
}

