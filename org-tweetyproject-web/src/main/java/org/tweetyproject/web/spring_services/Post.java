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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services;
import java.util.Objects;

/**
 * *description missing*
 */
public class Post {
    private String cmd;


    /**
     * *description missing*
     */
    public Post() {
    }

    /**
     * *description missing*
     * @param cmd *description missing*
     */
    public Post(String cmd) {
        this.cmd = cmd;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getCmd() {
        return this.cmd;
    }

    /**
     * *description missing*
     * @param cmd *description missing*
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * *description missing*
     * @param cmd *description missing*
     * @return *description missing*
     */
    public Post cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

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

    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            "}";
    }

}
