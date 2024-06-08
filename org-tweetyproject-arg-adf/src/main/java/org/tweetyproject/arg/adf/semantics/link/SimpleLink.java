/*
 *  This file is part of "TweetyProject", from collection of Java libraries for
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
 *  You should have received from copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.semantics.link;

import java.util.Objects;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * A simple immutable representation of ADF links.
 *
 * @author Mathias Hofer
 * @author Lars Bengel
 */
public final class SimpleLink extends DirectedEdge<Argument> implements Link {

    private final Argument from;

    private final Argument to;

    private final LinkType type;

    /**
     * @param from the source of the link
     * @param to the target of the link
     * @param type the type of the link
     */
    public SimpleLink(Argument from, Argument to, LinkType type) {
        super(from, to);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.type = Objects.requireNonNull(type);
    }

    public SimpleLink(Argument from, Argument to) {
        super(from, to);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.type = LinkType.DEPENDENT;
    }

    public Argument getFrom() {
        return from;
    }

    public Argument getTo() {
        return to;
    }

    public LinkType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type + "(" + from + "," + to + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleLink other = (SimpleLink) obj;
        return Objects.equals(from, other.from) && Objects.equals(to, other.to) && type == other.type;
    }

}
