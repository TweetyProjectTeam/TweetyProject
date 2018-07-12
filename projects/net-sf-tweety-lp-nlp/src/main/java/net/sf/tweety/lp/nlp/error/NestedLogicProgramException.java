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
package net.sf.tweety.lp.nlp.error;

import net.sf.tweety.logics.commons.error.LanguageException;

/**
 * NestedLogicProramException encapsulates those LanugageException that occur
 * in nested logic programs. It allows easier creation of language specific
 * exceptions. The language registered at the base exception is 
 * "Nested Logic Programs".
 * 
 * @author Tim Janus
 */
public class NestedLogicProgramException extends LanguageException {

	/** kill warning */
	private static final long serialVersionUID = 4406781843927755406L;
	
	public NestedLogicProgramException(LanguageExceptionReason reason) {
		super("Nested Logic Programs", reason);
	}
	
	public NestedLogicProgramException(LanguageExceptionReason reason, 
			String furtherInformation) {
		super("Nested Logic Programs", reason, furtherInformation);
	}
}
