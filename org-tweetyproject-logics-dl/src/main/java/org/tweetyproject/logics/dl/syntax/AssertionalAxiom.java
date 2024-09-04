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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.dl.syntax;

/**
 * Abstract base class for assertional axioms (concept assertions and role assertions)
 * in description logic.
 *
 * @see DlAxiom
 * @see org.tweetyproject.logics.dl.syntax.ConceptAssertion
 * @see org.tweetyproject.logics.dl.syntax.RoleAssertion
 *
 * @author Anna Gessler
 */
public abstract class AssertionalAxiom extends DlAxiom {

    /**
     * Default constructor for {@code AssertionalAxiom}.
     * <p>
     * This constructor is used to initialize an instance of the abstract base class
     * for assertional axioms in description logic. Concrete subclasses will specify
     * the specific nature of the assertion.
     * </p>
     */
    public AssertionalAxiom() {
        // Default constructor with no specific initialization
    }

	/**
	 * Determines if the concept involved in the assertion is atomic.
	 * @return {@code true} if the concept of the assertion is atomic,
	 *         {@code false} if it is a complex concept
	 */
	public abstract boolean isAtomic();

}
