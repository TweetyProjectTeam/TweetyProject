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
package org.tweetyproject.web.spring_services;
import java.util.Objects;
/**
 * Represents a simple data structure for handling "ping" messages. Each Ping object
 * has an identifier (id) and content. This class is commonly used for testing the
 * connectivity or response of a service.
 */

public class Ping {

    private final long id;
    private final String content;

    /**
     * Default constructor for the Ping class. Initializes id to -1 and content to an empty string.
     */
    public Ping() {
        this.id = -1;
        this.content = "";
    }

    /**
     * Constructor for the Ping class with specified id and content.
     *
     * @param id      The identifier for the Ping object.
     * @param content The content of the Ping object.
     */
    public Ping(long id, String content) {
        this.id = id;
        this.content = content;
    }

    /**
     * Gets the identifier of the Ping object.
     *
     * @return The identifier (id) of the Ping object.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the content of the Ping object.
     *
     * @return The content of the Ping object.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Equality is based on
     * the equality of the id and content fields.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Ping)) {
            return false;
        }
        Ping ping = (Ping) o;
        return id == ping.id && Objects.equals(content, ping.content);
    }

    /**
     * Returns a hash code value for the Ping object. The hash code is based on the id and content fields.
     *
     * @return A hash code value for the Ping object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    /**
     * Returns a string representation of the Ping object. The string representation consists of
     * the class name and the id and content fields.
     *
     * @return A string representation of the Ping object.
     */
    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
}