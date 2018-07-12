/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
