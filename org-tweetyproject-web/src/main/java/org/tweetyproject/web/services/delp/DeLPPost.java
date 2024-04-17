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
    public DeLPPost(String cmd, String email, String compcriterion, String kb, String query, int timeout, String unit_timeout) {
        this.cmd = cmd;
        this.email = email;
        this.compcriterion = compcriterion;
        this.kb = kb;
        this.query = query;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
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
     * @return *description missing*
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * *description missing*
     * @param email *description missing*
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getCompcriterion() {
        return this.compcriterion;
    }

    /**
     * *description missing*
     * @param compcriterion *description missing*
     */
    public void setCompcriterion(String compcriterion) {
        this.compcriterion = compcriterion;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getKb() {
        return this.kb;
    }

    /**
     * *description missing*
     * @param kb *description missing*
     */
    public void setKb(String kb) {
        this.kb = kb;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * *description missing*
     * @param query *description missing*
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * *description missing*
     * @param timeout *description missing*
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getUnit_timeout() {
        return this.unit_timeout;
    }

    /**
     * *description missing*
     * @param unit_timeout *description missing*
     */
    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }

    public DeLPPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    public DeLPPost email(String email) {
        setEmail(email);
        return this;
    }

    public DeLPPost compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    public DeLPPost kb(String kb) {
        setKb(kb);
        return this;
    }

    public DeLPPost query(String query) {
        setQuery(query);
        return this;
    }

    public DeLPPost timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

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
        return Objects.equals(cmd, delpPost.cmd) && Objects.equals(email, delpPost.email) && Objects.equals(compcriterion, delpPost.compcriterion) && Objects.equals(kb, delpPost.kb) && Objects.equals(query, delpPost.query) && timeout == delpPost.timeout && Objects.equals(unit_timeout, delpPost.unit_timeout);
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
