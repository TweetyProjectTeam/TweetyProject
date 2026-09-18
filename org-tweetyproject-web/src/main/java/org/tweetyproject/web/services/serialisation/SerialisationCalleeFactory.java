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
package org.tweetyproject.web.services.serialisation;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

/**
 * The SerialisationCalleeFactory class is responsible for creating instances of Callee
 * based on the specified Command, DungTheory, Extension, Selection and termination function parameters.
 * It also defines an enumeration of commands with associated IDs and labels.
 */
public class SerialisationCalleeFactory {
    /**
     * Enumeration of commands supported by the factory, each with a unique ID and label.
     */
    public enum Command {

        /** get reduct */
        GET_REDUCT("get_reduct", "Get Reduct for the given state"),
        /** is the state terminal */
        IS_TERMINAL("is_terminal", "Determine whether the given state is terminal"),
        /** get selection of initial sets */
        GET_SELECTION("get_selection", "Get the selectable initial sets of the state"),
        /** get models */
        GET_MODELS("get_models", "Get the extensions for the given functions"),
        /** get sequences */
        GET_SEQUENCES("get_sequences", "Get all serialisation sequences for the given functions");

        /** ID of the command */
        public final String id;
        /** Label of the command */
        public final String label;

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
     * Creates and returns a Callee instance based on the provided Command, AbstractExtensionReasoner,
     * and DungTheory parameters.
     *
     * @param cmd       The command to be executed
     * @param alpha     The selection function
     * @param beta      The termination function
     * @param bbase     The DungTheory to be processed
     * @param extension The extension to be processed
     * @return A Callee instance corresponding to the specified command
     * @throws RuntimeException If the specified command is not found
     */
    public static Callee getCallee(Command cmd, SelectionFunction alpha, TerminationFunction beta, DungTheory bbase, Extension<DungTheory> extension) {
        switch (cmd) {
            case GET_REDUCT -> {
                return new SerialisationGetReductCallee(bbase, extension);
            } case IS_TERMINAL -> {
                return new SerialisationIsTerminalCallee(bbase, extension, beta);
            } case GET_SELECTION -> {
                return new SerialisationGetSelectionCallee(bbase, alpha);
            } case GET_MODELS -> {
                return new SerialisationGetModelsCallee(bbase, alpha, beta);
            } case GET_SEQUENCES -> {
                return new SerialisationGetSequencesCallee(bbase, alpha, beta);
            } default -> throw new RuntimeException("Command not found: " + cmd);
        }
    }
}
