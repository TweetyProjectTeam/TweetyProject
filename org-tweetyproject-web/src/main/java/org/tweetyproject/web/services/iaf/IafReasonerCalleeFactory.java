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
package org.tweetyproject.web.services.iaf;

import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;


/**
 * The DungReasonerCalleeFactory class is responsible for creating instances of Callee
 * based on the specified Command, IncompleteReasoner, and IncompleteTheory parameters.
 * It also defines an enumeration of commands with associated IDs and labels.
 */
public class IafReasonerCalleeFactory {

    /**
     * Enumeration of commands supported by the factory, each with a unique ID and label.
     */
    public enum Command {
        /** get possible models */
        GET_MODELS_POS("get_models_pos", "Get all possible models"),
        /** get necessary models */
        GET_MODELS_NEC("get_models_nec", "Get all necessary models"),
        /** get possibly credulously acceptable arguments */
        GET_CREDULOUS_POS("get_credulous_pos", "Get possible credulous arguments"),
        /** get possibly skeptically acceptable arguments */
        GET_SKEPTICAL_POS("get_skeptical_pos", "Get possible skeptical arguments"),
        /** get necessary credulously acceptable arguments */
        GET_CREDULOUS_NEC("get_credulous_nec", "Get necessary credulous arguments"),
        /** get necessary skeptically acceptable arguments */
        GET_SKEPTICAL_NEC("get_skeptical_nec", "Get necessary skeptical arguments");

        /** ID of the command */
        public String id;
        /** Label of the command */
        public String label;

        /**
         * Constructor for Command enum.
         *
         * @param id    ID of the command
         * @param label Label of the command
         */
        Command(String id, String label) {
            this.id = id;
            this.label = label;
        }

        /**
         * Retrieves the Command enum based on the provided ID.
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
     * Retrieves an array of all available Command enums.
     *
     * @return An array of Command enums
     */
    public static Command[] getCommands() {
        return Command.values();
    }

    /**
     * Creates and returns a Callee instance based on the provided Command, IncompleteReasoner,
     * and IncompleteTheory parameters.
     *
     * @param cmd      The command to be executed
     * @param reasoner The IncompleteReasoner to be used
     * @param bbase    The IncompleteTheory to be processed
     * @return A Callee instance corresponding to the specified command
     * @throws RuntimeException If the specified command is not found
     */
    public static Callee getCallee(Command cmd, IncompleteReasoner reasoner, IncompleteTheory bbase) {
        switch (cmd) {
            case GET_MODELS_POS:
                return new IafReasonerGetModelsCallee(reasoner, bbase, IncompleteReasoner.Type.POSSIBLE);
            case GET_MODELS_NEC:
                return new IafReasonerGetModelsCallee(reasoner, bbase, IncompleteReasoner.Type.NECESSARY);
            case GET_CREDULOUS_POS:
                return new IafReasonerQueryAllCallee(reasoner, bbase, InferenceMode.CREDULOUS, IncompleteReasoner.Type.POSSIBLE);
            case GET_SKEPTICAL_POS:
                return new IafReasonerQueryAllCallee(reasoner, bbase, InferenceMode.SKEPTICAL, IncompleteReasoner.Type.POSSIBLE);
            case GET_CREDULOUS_NEC:
                return new IafReasonerQueryAllCallee(reasoner, bbase, InferenceMode.CREDULOUS, IncompleteReasoner.Type.NECESSARY);
            case GET_SKEPTICAL_NEC:
                return new IafReasonerQueryAllCallee(reasoner, bbase, InferenceMode.SKEPTICAL, IncompleteReasoner.Type.NECESSARY);
            default:
                throw new RuntimeException("Command not found: " + cmd.toString());
        }
    }
}
