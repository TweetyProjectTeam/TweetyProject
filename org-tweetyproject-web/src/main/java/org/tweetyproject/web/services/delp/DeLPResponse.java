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

import org.tweetyproject.web.services.Response;

/**
 * The DeLPResponse class extends the Response class and represents
 * a response containing information related to a Defeasible Logic Program
 * (DeLP) operation.
 */
public class DeLPResponse extends Response {

    /** The reply message in the DeLP response */
    private String reply;

    /** The email associated with the DeLP response */
    private String email;

    /** The completeness criterion specified in the DeLP response */
    private String compcriterion;

    /** The knowledge base (KB) provided in the DeLP response */
    private String kb;

    /** The query string in the DeLP response */
    private String query;

    /** The timeout value (in seconds) specified in the DeLP response */
    private int timeout;

    /** The answer provided by the DeLP response */
    private String answer;

    /** The time taken for the DeLP operation */
    private double time;

    /** The unit time specified in the DeLP response */
    private String unit_time;

    /** The status of the DeLP response */
    private String status;

    /**
     * Default constructor for DeLPResponse.
     */
    public DeLPResponse() {
    }

    /**
     * Parameterized constructor for DeLPResponse.
     *
     * @param reply         The reply message
     * @param email         The email associated with the response
     * @param compcriterion The completeness criterion
     * @param kb            The knowledge base (KB)
     * @param query         The query string
     * @param timeout       The timeout value (in seconds)
     * @param answer        The answer provided
     * @param time          The time taken for the operation
     * @param unit_time     The unit time specified
     * @param status        The status of the response
     */
    public DeLPResponse(String reply, String email, String compcriterion, String kb, String query,
            int timeout, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.compcriterion = compcriterion;
        this.kb = kb;
        this.query = query;
        this.timeout = timeout;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * Gets the reply message in the DeLP response.
     *
     * @return The reply message in the DeLP response.
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply message in the DeLP response.
     *
     * @param reply The reply message in the DeLP response.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email associated with the DeLP response.
     *
     * @return The email associated with the DeLP response.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the DeLP response.
     *
     * @param email The email associated with the DeLP response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the completeness criterion specified in the DeLP response.
     *
     * @return The completeness criterion specified in the DeLP response.
     */
    public String getCompcriterion() {
        return compcriterion;
    }

    /**
     * Sets the completeness criterion specified in the DeLP response.
     *
     * @param compcriterion The completeness criterion specified in the DeLP
     *                      response.
     */
    public void setCompcriterion(String compcriterion) {
        this.compcriterion = compcriterion;
    }

    /**
     * Gets the knowledge base (KB) provided in the DeLP response.
     *
     * @return The knowledge base (KB) provided in the DeLP response.
     */
    public String getKb() {
        return kb;
    }

    /**
     * Sets the knowledge base (KB) provided in the DeLP response.
     *
     * @param kb The knowledge base (KB) provided in the DeLP response.
     */
    public void setKb(String kb) {
        this.kb = kb;
    }

    /**
     * Gets the query string in the DeLP response.
     *
     * @return The query string in the DeLP response.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query string in the DeLP response.
     *
     * @param query The query string in the DeLP response.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the timeout value (in seconds) specified in the DeLP response.
     *
     * @return The timeout value (in seconds) specified in the DeLP response.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout value (in seconds) specified in the DeLP response.
     *
     * @param timeout The timeout value (in seconds) specified in the DeLP response.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the answer provided by the DeLP response.
     *
     * @return The answer provided by the DeLP response.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer provided by the DeLP response.
     *
     * @param answer The answer provided by the DeLP response.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets the time taken for the DeLP operation.
     *
     * @return The time taken for the DeLP operation.
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the time taken for the DeLP operation.
     *
     * @param time The time taken for the DeLP operation.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the unit time specified in the DeLP response.
     *
     * @return The unit time specified in the DeLP response.
     */
    public String getUnit_time() {
        return unit_time;
    }

    /**
     * Sets the unit time specified in the DeLP response.
     *
     * @param unit_time The unit time specified in the DeLP response.
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * Gets the status of the DeLP response.
     *
     * @return The status of the DeLP response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the DeLP response.
     *
     * @param status The status of the DeLP response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the reply message in the DeLP response.
     *
     * @param reply The reply message in the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email associated with the DeLP response.
     *
     * @param email The email associated with the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the completeness criterion specified in the DeLP response.
     *
     * @param compcriterion The completeness criterion specified in the DeLP
     *                      response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    /**
     * Sets the knowledge base (KB) provided in the DeLP response.
     *
     * @param kb The knowledge base (KB) provided in the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * Sets the query string in the DeLP response.
     *
     * @param query The query string in the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse query(String query) {
        setQuery(query);
        return this;
    }

    /**
     * Sets the timeout value (in seconds) specified in the DeLP response.
     *
     * @param timeout The timeout value (in seconds) specified in the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    /**
     * Sets the answer provided by the DeLP response.
     *
     * @param answer The answer provided by the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * Sets the time taken for the DeLP operation.
     *
     * @param time The time taken for the DeLP operation.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse time(double time) {
        setTime(time);
        return this;
    }

    /**
     * Sets the unit time specified in the DeLP response.
     *
     * @param unit_time The unit time specified in the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    /**
     * Sets the status of the DeLP response.
     *
     * @param status The status of the DeLP response.
     * @return The current instance of DeLPResponse.
     */
    public DeLPResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DeLPResponse)) {
            return false;
        }
        DeLPResponse delpResponse = (DeLPResponse) o;
        return Objects.equals(reply, delpResponse.reply) && Objects.equals(email, delpResponse.email)
                && Objects.equals(compcriterion, delpResponse.compcriterion) && Objects.equals(kb, delpResponse.kb)
                && Objects.equals(query, delpResponse.query) && timeout == delpResponse.timeout
                && Objects.equals(answer, delpResponse.answer) && time == delpResponse.time
                && Objects.equals(unit_time, delpResponse.unit_time) && Objects.equals(status, delpResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, compcriterion, kb, query, timeout, answer, time, unit_time, status);
    }

    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", compcriterion='" + getCompcriterion() + "'" +
                ", kb='" + getKb() + "'" +
                ", query='" + getQuery() + "'" +
                ", timeout='" + getTimeout() + "'" +
                ", answer='" + getAnswer() + "'" +
                ", time='" + getTime() + "'" +
                ", unit_time='" + getUnit_time() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }

}