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
package org.tweetyproject.web.services;
import java.util.Objects;

/**
 * Represents a generic data structure for HTTP POST requests. It encapsulates a command
 * string, which is commonly used in the payload of POST requests. This class is typically
 * used to simulate or construct POST request payloads within service-oriented architectures.
 * @author Jonas Klein
 */
public class Post {
    private String cmd;

    /**
     * Default constructor for the Post class. Initializes an empty command string.
     */
    public Post() {
        this.cmd = "";
    }

    /**
     * Constructs a Post object with a specific command.
     *
     * @param cmd The command string to be used as part of the POST request payload.
     */
    public Post(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Retrieves the command string of this Post object.
     *
     * @return The command string contained in this Post object.
     */
    public String getCmd() {
        return this.cmd;
    }

    /**
     * Sets the command string for this Post object.
     *
     * @param cmd The command string to set for this Post object.
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Fluent API to set the command of this Post object and return the instance for chaining.
     *
     * @param cmd The command string to set.
     * @return This Post object to enable method chaining.
     */
    public Post cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    /**
     * Determines whether this Post object is equal to another object. Two Post objects are considered equal
     * if their command strings are the same.
     *
     * @param o The object to compare with this Post.
     * @return {@code true} if the specified object is a Post with the same command; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(cmd, post.cmd);
    }

    /**
     * Returns a string representation of this Post object.
     *
     * @return A string representation of this Post, consisting of its command.
     */
    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            "}";
    }

}
