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
package org.tweetyproject.web.spring_services.delp;

import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.web.spring_services.Callee;

/**
 * The DeLPCallee class represents a callee for querying a Defeasible Logic Program (DeLP)
 * using a specified DefeasibleLogicProgram, DelpReasoner, and Formula.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the query operation using the provided DeLP and Formula with a DelpReasoner.
 */
public class DeLPCallee extends Callee {

    /** The DefeasibleLogicProgram to be queried */
    private DefeasibleLogicProgram delp;

    /** The DelpReasoner used for querying the DefeasibleLogicProgram */
    private DelpReasoner reasoner;

    /** The Formula representing the query */
    private Formula f;

    /**
     * Constructs a new DeLPCallee with the specified DefeasibleLogicProgram, DelpReasoner, and Formula.
     *
     * @param delp     The DefeasibleLogicProgram to be queried
     * @param reasoner The DelpReasoner used for querying the DefeasibleLogicProgram
     * @param f        The Formula representing the query
     */
    public DeLPCallee(DefeasibleLogicProgram delp, DelpReasoner reasoner, Formula f) {
        this.delp = delp;
        this.reasoner = reasoner;
        this.f = f;
    }

    /**
     * Executes the query operation using the specified DefeasibleLogicProgram, DelpReasoner, and Formula.
     *
     * @return The result of the query operation, represented by DelpAnswer.Type
     * @throws Exception If an error occurs during the query operation
     */
    @Override
    public DelpAnswer.Type call() throws Exception {
        return this.reasoner.query(this.delp, (FolFormula) this.f);
    }
}

