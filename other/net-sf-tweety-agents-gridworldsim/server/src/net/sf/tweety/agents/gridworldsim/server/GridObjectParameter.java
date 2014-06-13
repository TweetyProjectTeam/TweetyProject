/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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

/**
 * Objects made from this class represent the parameter of a {@link GridObject}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObjectParameter {

    private final String name;
    private final String value;

    /**
     * Constructs a new {@code GridObjectParameter}.
     * @param name the name of the {@code GridObjectParameter}.
     * @param value the value of the {@code GridObjectParameter}
     */
    public GridObjectParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get the name of this {@code GridObjectParameter}.
     * @return the name of this {@code GridObjectParameter}
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this {@code GridObjectParameter}.
     * @return the value of this {@code GridObjectParameter}
     */
    public String getValue() {
        return value;
    }
}
