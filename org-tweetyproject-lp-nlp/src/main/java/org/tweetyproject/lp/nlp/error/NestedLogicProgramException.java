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
package org.tweetyproject.lp.nlp.error;

import org.tweetyproject.logics.commons.error.LanguageException;

/**
 * NestedLogicProramException encapsulates those LanugageException that occur
 * in nested logic programs. It allows easier creation of language specific
 * exceptions. The language registered at the base exception is
 * "Nested Logic Programs".
 *
 * @author Tim Janus
 */
public class NestedLogicProgramException extends LanguageException {

	    /** Serial version UID for this exception class. */
		private static final long serialVersionUID = 4406781843927755406L;

		/**
		 * Constructs a new `NestedLogicProgramException` with the specified reason.
		 *
		 * <p>
		 * This constructor initializes the exception with a specific reason for the
		 * error, which is provided by the `LanguageExceptionReason` enum. The language
		 * is automatically set to "Nested Logic Programs".
		 * </p>
		 *
		 * @param reason the reason for the exception, represented by an enum value
		 *               from `LanguageExceptionReason`.
		 */
		public NestedLogicProgramException(LanguageExceptionReason reason) {
			super("Nested Logic Programs", reason);
		}

		/**
		 * Constructs a new `NestedLogicProgramException` with the specified reason
		 * and additional information.
		 *
		 * <p>
		 * This constructor initializes the exception with a specific reason for the
		 * error and additional information that provides more context about the error.
		 * The language is automatically set to "Nested Logic Programs".
		 * </p>
		 *
		 * @param reason the reason for the exception, represented by an enum value
		 *               from `LanguageExceptionReason`.
		 * @param furtherInformation additional information about the error, typically
		 *                           used to provide more specific details in the error message.
		 */
		public NestedLogicProgramException(LanguageExceptionReason reason,
				String furtherInformation) {
			super("Nested Logic Programs", reason, furtherInformation);

}
}
