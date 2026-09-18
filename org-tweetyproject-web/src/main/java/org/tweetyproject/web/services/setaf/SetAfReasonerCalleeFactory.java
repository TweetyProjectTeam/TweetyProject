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
package org.tweetyproject.web.services.setaf;

import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;


/**
 * The SetAfReasonerCalleeFactory class is responsible for creating instances of Callee
 * based on the specified Command, AbstractExtensionReasoner, and SetAF parameters.
 * It also defines an enumeration of commands with associated IDs and labels.
 */
public class SetAfReasonerCalleeFactory {

    /**
     * Enumeration of commands supported by the factory, each with a unique ID and label.
     */
    public enum Command {
        /** get models */
        GET_MODELS("get_models", "Get all models"),
        /** get credulously acceptable arguments */
        GET_CREDULOUS("get_credulous", "Get credulous arguments"),
        /** get skeptically acceptable arguments */
        GET_SKEPTICAL("get_skeptical", "Get skeptical arguments");

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
     * Creates and returns a Callee instance based on the provided Command, AbstractSetAfExtensionReasoner,
     * and SetAf parameters.
     *
     * @param cmd      The command to be executed
     * @param reasoner The AbstractSetAfExtensionReasoner to be used
     * @param bbase    The SetAf to be processed
     * @return A Callee instance corresponding to the specified command
     * @throws RuntimeException If the specified command is not found
     */
    public static Callee getCallee(Command cmd, AbstractSetAfExtensionReasoner reasoner, SetAf bbase) {
        switch (cmd) {
            case GET_MODELS:
                return new SetAfReasonerGetModelsCallee(reasoner, bbase);
            case GET_CREDULOUS:
                return new SetAfReasonerQueryAllCallee(reasoner, bbase, InferenceMode.CREDULOUS);
            case GET_SKEPTICAL:
                return new SetAfReasonerQueryAllCallee(reasoner, bbase, InferenceMode.SKEPTICAL);
            default:
                throw new RuntimeException("Command not found: " + cmd.toString());
        }
    }
}
