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
package org.tweetyproject.web.spring_services.incmes;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Post;

/**
 * Represents a data transfer object for inconsistency calculation requests.
 *
 * The InconsistencyPost class extends the Post class and encapsulates information
 * about the parameters required for an inconsistency calculation, including the command,
 * user email, inconsistency measure, knowledge base, response format, timeout, and unit timeout.
 *
 * This class provides getter and setter methods for accessing and modifying its attributes.
 * Additionally, convenience methods are included for chaining attribute setting operations.
 * The class overrides the equals, hashCode, and toString methods for proper comparison and string representation.
 *
 * @see Post
 * @see Objects
 */

public class InconsistencyPost extends Post {

    private String cmd;
    private String email;
    private String measure;
    private String kb;
    private String format;
    private int timeout;
    private String unit_timeout;


    public InconsistencyPost() {
    }

    public InconsistencyPost(String cmd, String email, String measure, String kb, String format, int timeout, String unit_timeout) {
        this.cmd = cmd;
        this.email = email;
        this.measure = measure;
        this.kb = kb;
        this.format = format;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMeasure() {
        return this.measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUnit_timeout() {
        return this.unit_timeout;
    }

    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }

    public InconsistencyPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    public InconsistencyPost email(String email) {
        setEmail(email);
        return this;
    }

    public InconsistencyPost measure(String measure) {
        setMeasure(measure);
        return this;
    }

    public InconsistencyPost kb(String kb) {
        setKb(kb);
        return this;
    }

    public InconsistencyPost format(String format) {
        setFormat(format);
        return this;
    }

    public InconsistencyPost timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    public InconsistencyPost unit_timeout(String unit_timeout) {
        setUnit_timeout(unit_timeout);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InconsistencyPost)) {
            return false;
        }
        InconsistencyPost inconsistencyPost = (InconsistencyPost) o;
        return Objects.equals(cmd, inconsistencyPost.cmd) && Objects.equals(email, inconsistencyPost.email) && Objects.equals(measure, inconsistencyPost.measure) && Objects.equals(kb, inconsistencyPost.kb) && Objects.equals(format, inconsistencyPost.format) && timeout == inconsistencyPost.timeout && Objects.equals(unit_timeout, inconsistencyPost.unit_timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, email, measure, kb, format, timeout, unit_timeout);
    }

    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            ", email='" + getEmail() + "'" +
            ", measure='" + getMeasure() + "'" +
            ", kb='" + getKb() + "'" +
            ", format='" + getFormat() + "'" +
            ", timeout='" + getTimeout() + "'" +
            ", unit_timeout='" + getUnit_timeout() + "'" +
            "}";
    }


}
