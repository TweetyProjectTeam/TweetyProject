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
package org.tweetyproject.web.services.delp;

import java.util.Objects;

/**
 * The DeLPPost class represents a data structure for holding information
 * related to a Defeasible Logic Program (DeLP) request sent via HTTP POST.
 */
public class DeLPPost {

    /** The command type for the DeLP request */
    private String cmd;

    /** The email associated with the DeLP request */
    private String email;

    /** The completeness criterion specified in the DeLP request */
    private String compcriterion;

    /** The knowledge base (KB) provided in the DeLP request */
    private String kb;

    /** The query string in the DeLP request */
    private String query;

    /** The timeout value (in seconds) specified in the DeLP request */
    private int timeout;

    /** The unit timeout value specified in the DeLP request */
    private String unit_timeout;

    /**
     * Default constructor for DeLPPost.
     */
    public DeLPPost() {
    }

    /**
     * Parameterized constructor for DeLPPost.
     *
     * @param cmd           The command type
     * @param email         The email associated with the request
     * @param compcriterion The completeness criterion
     * @param kb            The knowledge base (KB)
     * @param query         The query string
     * @param timeout       The timeout value (in seconds)
     * @param unit_timeout  The unit timeout value
     */
    public DeLPPost(String cmd, String email, String compcriterion, String kb, String query, int timeout,
            String unit_timeout) {
        this.cmd = cmd;
        this.email = email;
        this.compcriterion = compcriterion;
        this.kb = kb;
        this.query = query;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
    }

    /**
     * Gets the command type for the DeLP request.
     *
     * @return The command type for the DeLP request.
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Sets the command type for the DeLP request.
     *
     * @param cmd The command type for the DeLP request.
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Gets the email associated with the DeLP request.
     *
     * @return The email associated with the DeLP request.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the DeLP request.
     *
     * @param email The email associated with the DeLP request.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the completeness criterion specified in the DeLP request.
     *
     * @return The completeness criterion specified in the DeLP request.
     */
    public String getCompcriterion() {
        return compcriterion;
    }

    /**
     * Sets the completeness criterion specified in the DeLP request.
     *
     * @param compcriterion The completeness criterion specified in the DeLP
     *                      request.
     */
    public void setCompcriterion(String compcriterion) {
        this.compcriterion = compcriterion;
    }

    /**
     * Gets the knowledge base (KB) provided in the DeLP request.
     *
     * @return The knowledge base (KB) provided in the DeLP request.
     */
    public String getKb() {
        return kb;
    }

    /**
     * Sets the knowledge base (KB) provided in the DeLP request.
     *
     * @param kb The knowledge base (KB) provided in the DeLP request.
     */
    public void setKb(String kb) {
        this.kb = kb;
    }

    /**
     * Gets the query string in the DeLP request.
     *
     * @return The query string in the DeLP request.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query string in the DeLP request.
     *
     * @param query The query string in the DeLP request.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the timeout value (in seconds) specified in the DeLP request.
     *
     * @return The timeout value (in seconds) specified in the DeLP request.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout value (in seconds) specified in the DeLP request.
     *
     * @param timeout The timeout value (in seconds) specified in the DeLP request.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the unit timeout value specified in the DeLP request.
     *
     * @return The unit timeout value specified in the DeLP request.
     */
    public String getUnit_timeout() {
        return unit_timeout;
    }

    /**
     * Sets the unit timeout value specified in the DeLP request.
     *
     * @param unit_timeout The unit timeout value specified in the DeLP request.
     */
    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }

    /**
     * Sets the command type for the DeLP request.
     *
     * @param cmd The command type for the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    /**
     * Sets the email associated with the DeLP request.
     *
     * @param email The email associated with the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the completeness criterion specified in the DeLP request.
     *
     * @param compcriterion The completeness criterion specified in the DeLP
     *                      request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    /**
     * Sets the knowledge base (KB) provided in the DeLP request.
     *
     * @param kb The knowledge base (KB) provided in the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * Sets the query string in the DeLP request.
     *
     * @param query The query string in the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost query(String query) {
        setQuery(query);
        return this;
    }

    /**
     * Sets the timeout value (in seconds) specified in the DeLP request.
     *
     * @param timeout The timeout value (in seconds) specified in the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    /**
     * Sets the unit timeout value specified in the DeLP request.
     *
     * @param unit_timeout The unit timeout value specified in the DeLP request.
     * @return The current instance of DeLPPost.
     */
    public DeLPPost unit_timeout(String unit_timeout) {
        setUnit_timeout(unit_timeout);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DeLPPost)) {
            return false;
        }
        DeLPPost delpPost = (DeLPPost) o;
        return Objects.equals(cmd, delpPost.cmd) && Objects.equals(email, delpPost.email)
                && Objects.equals(compcriterion, delpPost.compcriterion) && Objects.equals(kb, delpPost.kb)
                && Objects.equals(query, delpPost.query) && timeout == delpPost.timeout
                && Objects.equals(unit_timeout, delpPost.unit_timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, email, compcriterion, kb, query, timeout, unit_timeout);
    }

    @Override
    public String toString() {
        return "{" +
                " cmd='" + getCmd() + "'" +
                ", email='" + getEmail() + "'" +
                ", compcriterion='" + getCompcriterion() + "'" +
                ", kb='" + getKb() + "'" +
                ", query='" + getQuery() + "'" +
                ", timeout='" + getTimeout() + "'" +
                ", unit_timeout='" + getUnit_timeout() + "'" +
                "}";
    }

}
