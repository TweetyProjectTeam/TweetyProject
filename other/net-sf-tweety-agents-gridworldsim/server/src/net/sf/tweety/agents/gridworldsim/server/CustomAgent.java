package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This is an example {@link Agent} to demonstrate that the use of custom {@link Agent} classes works. It does nothing different
 * from a normal {@link Agent} except that is always and exclusively has the property "custom agent".
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class CustomAgent extends Agent {

    /**
     * Constructs a new {@code CustomAgent}
     */
    public CustomAgent() {
        super();
    }

    /**
     * Get the properties of this {@code CustomAgent}.
     * @param visibleOnly does not matter for this {@code CustomAgent}
     * @return a {@code String} {@code Collection} containing the {@code String} "Custom agent!"
     */
    @Override
    public Collection<String> getProperties(boolean visibleOnly) {
        Collection<String> test = new LinkedList<String>();
        test.add("Custom agent!");
        return test;
    }
}
