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
package org.tweetyproject.web.services.adf;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.*;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;

import java.io.IOException;
import java.util.List;

/**
 * Abstract factory for retrieving ADF reasoners.
 *
 * @author Lars Bengel
 */
public abstract class AbstractAdfReasonerFactory {

    /**
     * Prevents instantiation.
     */
    protected AbstractAdfReasonerFactory() {
    }

    /**
     * Returns an array of all available semantics.
     *
     * @return An array of all available semantics.
     */
    public static Semantics[] getSemantics() {
        return new Semantics[]{Semantics.CF,Semantics.ADM,Semantics.CO,Semantics.GR,Semantics.PR,Semantics.ST,Semantics.NA};
    }

    /**
     * Creates a new reasoner measure of the given semantics with default
     * settings.
     *
     * @param sem some identifier of a semantics.
     * @return the requested reasoner.
     */
    public static AbstractADFReasoner getReasoner(Semantics sem) {
        switch (sem) {
            case CF -> {
                return new ConflictFreeReasoner(new NativeMinisatSolver());
            } case ADM -> {
                return new AdmissibleReasoner(new NativeMinisatSolver());
            } case CO -> {
                return new CompleteReasoner(new NativeMinisatSolver());
            } case PR -> {
                return new PreferredReasoner(new NativeMinisatSolver());
            } case GR -> {
                return new GroundReasoner(new NativeMinisatSolver());
            } case ST -> {
                return new StableReasoner(new NativeMinisatSolver());
            } case NA -> {
                return new NaiveReasoner(new NativeMinisatSolver());
            } default -> throw new IllegalArgumentException("unsupported semantics");
        }
    }

    /**
     * Constructs a new ADF from the given information
     * @param nr_of_arguments   the number of arguments
     * @param conditions        the acceptance conditions as a list of KPP-strings
     * @return An ADF inferred from the given data
     */
    public static AbstractDialecticalFramework getAdf(int nr_of_arguments, List<String> conditions) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < nr_of_arguments; i++) {
            s.append(String.format("s(%s).\n", i+1));
        }
        for (String condition : conditions) {
            s.append(condition).append(".\n");
        }
        NativeMinisatSolver solver = new NativeMinisatSolver();
        LinkStrategy strat = new SatLinkStrategy(solver);
        KppADFFormatParser parser = new KppADFFormatParser(strat, true);
        try {
            return parser.parse(s.toString());
        } catch (IOException ignored) {
            throw new IllegalArgumentException("syntactical error in adf conditions");
        }
    }
}
