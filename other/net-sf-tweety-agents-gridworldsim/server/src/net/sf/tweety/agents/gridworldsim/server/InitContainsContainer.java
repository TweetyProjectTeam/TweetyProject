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

import org.w3c.dom.Element;

/**
 * This class is used in building the contains relations (which have arbitrary depth) at initialization.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class InitContainsContainer {

    private final Element objectElement;
    private final GridObject parent;
    private final GridObject rootObject;

    /**
     * Constructs a new {@code InitContainsContainer}.
     * @param objectElement the {@code Element} of the {@link GridObject} that should be processed when parsing the XML file
     * @param parent the parent of this {@link GridObject}
     * @param rootObject the root {@link GridObject} in the contains graph
     */
    public InitContainsContainer(Element objectElement, GridObject parent, GridObject rootObject) {
        this.objectElement = objectElement;
        this.parent = parent;
        this.rootObject = rootObject;
    }

    /**
     * Get the {@code Element} that is processed.
     * @return the {@code Element} that is processed
     */
    public Element getObjectElement() {
        return objectElement;
    }

    /**
     * Get the root {@link GridObject} in the contains graph.
     * @return the root {@link GridObject} in the contains graph
     */
    public GridObject getRootObject() {
        return rootObject;
    }

    /**
     * Get the parent {@link GridObject} of the currently processed {@code Element}.
     * @return the parent {@link GridObject} of the currently processed {@code Element}.
     */
    public GridObject getParent() {
        return parent;
    }
}
