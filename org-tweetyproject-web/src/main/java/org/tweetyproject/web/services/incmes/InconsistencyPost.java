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
package org.tweetyproject.web.services.incmes;
import java.util.Objects;

import org.tweetyproject.web.services.Post;

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

    /**
     * Constructs an empty {@code InconsistencyPost}.
     * Use this constructor to create a post object that will be populated later.
     */
    public InconsistencyPost() {
    }

    /**
     * Constructs an {@code InconsistencyPost} with specified values for each field.
     * 
     * @param cmd The command to be executed during the analysis.
     * @param email The email address associated with this post.
     * @param measure The specific measure or metric to be analyzed.
     * @param kb Knowledge base reference or identifier used during the analysis.
     * @param format The data format or structure that is being analyzed.
     * @param timeout The maximum allowed duration for the analysis to complete.
     * @param unit_timeout The unit of time used for the timeout, such as seconds or minutes.
     */
    public InconsistencyPost(String cmd, String email, String measure, String kb, String format, int timeout, String unit_timeout) {
        this.cmd = cmd;
        this.email = email;
        this.measure = measure;
        this.kb = kb;
        this.format = format;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
    }

    /**
     * Returns the command associated with this post.
     * 
     * @return The command as a string.
     */
    public String getCmd() {
        return this.cmd;
    }

    /**
     * Sets the command for this post.
     * 
     * @param cmd The command string to be set.
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Returns the email address associated with this post.
     * 
     * @return The email as a string.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address for this post.
     * 
     * @param email The email string to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the measure associated with this post.
     * 
     * @return The measure as a string.
     */
    public String getMeasure() {
        return this.measure;
    }

    /**
     * Sets the measure for this post.
     * 
     * @param measure The measure string to be set.
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * Returns the knowledge base identifier associated with this post.
     * 
     * @return The kb identifier as a string.
     */
    public String getKb() {
        return this.kb;
    }

    /**
     * Sets the knowledge base identifier for this post.
     * 
     * @param kb The kb string to be set.
     */
    public void setKb(String kb) {
        this.kb = kb;
    }

    /**
     * Returns the data format for this post.
     * 
     * @return The format as a string.
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Sets the data format for this post.
     * 
     * @param format The format string to be set.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Returns the timeout value for this post.
     * 
     * @return The timeout as an integer.
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Sets the timeout value for this post.
     * 
     * @param timeout The timeout value to be set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the unit of time for the timeout of this post.
     * 
     * @return The unit of timeout as a string.
     */
    public String getUnit_timeout() {
        return this.unit_timeout;
    }

    /**
     * Sets the unit of time for the timeout of this post.
     * 
     * @param unit_timeout The unit of timeout to be set.
     */
    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }


    /**
     * Sets the command for this post and returns this instance to allow method chaining.
     *
     * @param cmd The command to be executed, typically specifying the operation or action to perform.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    /**
     * Sets the email address associated with this post and returns this instance for method chaining.
     *
     * @param email The email address related to the user or context of the post.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the measure key, which specifies the metric or parameter to be analyzed, and returns this instance for method chaining.
     *
     * @param measure The measure or metric key that is the focus of the post.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost measure(String measure) {
        setMeasure(measure);
        return this;
    }

    /**
     * Sets the knowledge base identifier (kb) used in the analysis and returns this instance for method chaining.
     *
     * @param kb The identifier for the knowledge base relevant to the post.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * Sets the format of the data to be analyzed and returns this instance for method chaining.
     *
     * @param format The data format, detailing the structure or type of data that the post concerns.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Sets the timeout for the operation specified in the post and returns this instance for method chaining.
     *
     * @param timeout The maximum amount of time (in the unit specified by {@code unit_timeout}) that the operation should take.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
    public InconsistencyPost timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    /**
     * Sets the unit of time for the timeout and returns this instance for method chaining.
     *
     * @param unit_timeout The unit of time for the timeout, such as seconds or minutes.
     * @return This {@code InconsistencyPost} instance, to facilitate chaining multiple settings calls.
     */
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
