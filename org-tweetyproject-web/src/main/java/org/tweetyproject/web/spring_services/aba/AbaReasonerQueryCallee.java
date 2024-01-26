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
package org.tweetyproject.web.spring_services.aba;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.web.spring_services.Callee;

/**
 * The AbaReasonerQueryCallee class extends the Callee class and represents
 * a callee responsible for querying an assumption in Argumentation-Based Argumentation (ABA).
 *
 * @param <T> The type of formula used in ABA
 */
public class AbaReasonerQueryCallee<T extends Formula> extends Callee {

    /** The ABA reasoner instance */
    private GeneralAbaReasoner<T> reasoner;

    /** The ABA theory instance */
    private AbaTheory<T> bbase;

    /** The assumption used in the query */
    private Assumption<T> assumption;

    /**
     * Constructor for AbaReasonerQueryCallee.
     *
     * @param reasoner  The ABA reasoner instance
     * @param bbase     The ABA theory instance
     * @param assumption The assumption used in the query
     */
    public AbaReasonerQueryCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase, Assumption<T> assumption) {
        this.reasoner = reasoner;
        this.bbase = bbase;
        this.assumption = assumption;
    }

    /**
     * Calls the ABA reasoner to perform a query using the provided ABA theory and assumption.
     *
     * @return The Boolean result of the query
     * @throws Exception If an exception occurs during the ABA reasoner operation
     */
    @Override
    public Boolean call() throws Exception {
        return this.reasoner.query(this.bbase, assumption);
    }
}
