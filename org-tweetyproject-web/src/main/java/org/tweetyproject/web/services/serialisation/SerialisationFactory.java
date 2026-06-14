package org.tweetyproject.web.services.serialisation;

public class SerialisationFactory {

    public enum Command {
        GET_REDUCT("get_reduct", "Get Reduct for the given state"),
        IS_TERMINAL("is_terminal", "Determine whether the given state is terminal"),
        GET_SELECTION("get_selection", "Get the selectable initial sets of the state");

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
}
