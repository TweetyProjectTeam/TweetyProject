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
package org.tweetyproject.web.services.paf;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;

/**
 * Factory for creating {@link Callee} instances for the PAF web service.
 */
public class PafReasonerCalleeFactory {

    /** Commands supported by the PAF web service. */
    public enum Command {
        /** Query the credulous probability of an argument. */
        GET_CREDULOUS("get_credulous", "Get credulous probability of all arguments"),
        /** Query the skeptical probability of an argument. */
        GET_SKEPTICAL("get_skeptical", "Get skeptical probability of all arguments");

        /** String identifier used in the JSON request. */
        public final String id;
        /** Human-readable label. */
        public final String label;

        Command(String id, String label) {
            this.id = id;
            this.label = label;
        }

        /**
         * Looks up a command by its string identifier.
         *
         * @param id the command identifier
         * @return the matching {@link Command}, or {@code null} if not found
         */
        public static Command getCommand(String id) {
            for (Command c : Command.values())
                if (c.id.equals(id))
                    return c;
            return null;
        }
    }

    /**
     * Returns all available commands.
     *
     * @return array of commands
     */
    public static Command[] getCommands() {
        return Command.values();
    }

    /**
     * Creates the appropriate {@link Callee} for the given command.
     *
     * @param cmd       the command to execute
     * @param reasoner  the PAF reasoner to use
     * @param paf       the probabilistic argumentation framework
     * @param argument  the argument to query
     * @return the callee
     */
    public static Callee getCallee(Command cmd, AbstractPafReasoner reasoner,
            ProbabilisticArgumentationFramework paf) {
        switch (cmd) {
            case GET_CREDULOUS:
                return new PafReasonerQueryAllCallee(reasoner, paf, InferenceMode.CREDULOUS);
            case GET_SKEPTICAL:
                return new PafReasonerQueryAllCallee(reasoner, paf, InferenceMode.SKEPTICAL);
            default:
                throw new RuntimeException("Command not found: " + cmd);
        }
    }
}
