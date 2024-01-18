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
 * 
 * *description missing*
 */
public class Ping {

    private final long id;
	private final String content;

    /**
     * *description missing* 
     */
    public Ping() {
		this.id = -1;
		this.content = "";
	}

    /**
     * *description missing*
     * @param id *description missing*
     * @param content *description missing*
     */
    public Ping(long id, String content) {
        this.id = id;
        this.content = content;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public long getId() {
        return this.id;
    }


    /**
     * *description missing*
     * @return *description missing*
     */
    public String getContent() {
        return this.content;
    }


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

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }

}