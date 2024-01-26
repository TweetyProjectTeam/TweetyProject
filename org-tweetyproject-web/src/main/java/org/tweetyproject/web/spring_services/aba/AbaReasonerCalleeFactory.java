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


import java.util.Objects;

/**
 * The AbaReasonerCalleeFactory class provides a factory for creating different
 * instances of Argumentation-Based Argumentation (ABA) reasoner callees based on
 * specific commands.
 */
public class AbaReasonerCalleeFactory {

    /**
     * Enumeration of supported commands for ABA reasoner.
     */
    public enum Command {
        GET_MODELS("get_models", "Get all models"),
        QUERY("query", "Query ABA framework"),
        GET_MODEL("get_model", "Get some model");

        /** Identifier for the command */
        public String id;

        /** Label for the command */
        public String label;

        /**
         * Constructor for Command enumeration.
         *
         * @param id    ID for the command
         * @param label Label for the command
         */
        Command(String id, String label) {
            this.id = id;
            this.label = label;
        }

        /**
         * Gets the Command enum based on the provided ID.
         *
         * @param id ID of the command
         * @return The corresponding Command enum, or null if not found
         */
        public static Command getCommand(String id) {
            for (Command m : Command.values())
                if (m.id.equals(id))
                    return m;
            return null;
        }
    }

    /**
     * Gets an array of all supported commands.
     *
     * @return An array of Command enums
     */
    public static Command[] getCommands() {
        return Command.values();
    }

    /**
     * Creates a new callee instance based on the provided command, ABA reasoner,
     * ABA theory, and assumption.
     *
     * @param cmd      The command for which the callee is created
     * @param reasoner The ABA reasoner instance
     * @param bbase    The ABA theory instance
     * @param a        The assumption
     * @param <T>      The type of formula used in ABA
     * @return The created callee instance
     * @throws RuntimeException If the command is not found
     */
    public static <T extends Formula> Callee getCallee(Command cmd, GeneralAbaReasoner<T> reasoner,
                                                       AbaTheory<T> bbase, Assumption<T> a) {
        switch (cmd) {
            case GET_MODELS:
                return new AbaReasonerGetModelsCallee<>(reasoner, bbase);

            case GET_MODEL:
                return new AbaReasonerGetModelCallee<>(reasoner, bbase);

            case QUERY:
                return new AbaReasonerQueryCallee<>(reasoner, bbase, a);

            default:
                throw new RuntimeException("Command not found: " + Objects.requireNonNull(cmd).toString());
        }
    }
}
