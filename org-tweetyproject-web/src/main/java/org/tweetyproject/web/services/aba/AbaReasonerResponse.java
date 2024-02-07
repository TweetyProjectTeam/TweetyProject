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
package org.tweetyproject.web.services.aba;

import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * The AbaReasonerResponse class extends the Response class and represents the
 * response
 * data structure for an Argumentation-Based Argumentation (ABA) reasoner.
 */
public class AbaReasonerResponse extends Response {

    /** The reply from the ABA reasoner */
    private String reply;

    /** The email associated with the response */
    private String email;

    /** The knowledge base (KB) used in the ABA reasoner */
    private String kb;

    /** The format of the knowledge base (KB) */
    private String kb_format;

    /** The first-order logic (FOL) signature */
    private String fol_signature;

    /** The query assumption used in the ABA reasoner */
    private String query_assumption;

    /** The semantics used in the ABA reasoner */
    private String semantics;

    /** The timeout in seconds for the ABA reasoner operation */
    private int timeout;

    /** The answer/result of the ABA reasoner operation */
    private String answer;

    /** The execution time of the ABA reasoner operation */
    private double time;

    /** The unit time for the ABA reasoner operation */
    private String unit_time;

    /** The status of the ABA reasoner response */
    private String status;

    /**
     * Default constructor for AbaReasonerResponse.
     */
    public AbaReasonerResponse() {
    }

    /**
     * Parameterized constructor for AbaReasonerResponse.
     *
     * @param reply            The reply from the ABA reasoner
     * @param email            The email associated with the response
     * @param kb               The knowledge base (KB) used in the ABA reasoner
     * @param kb_format        The format of the knowledge base (KB)
     * @param fol_signature    The first-order logic (FOL) signature
     * @param query_assumption The query assumption used in the ABA reasoner
     * @param semantics        The semantics used in the ABA reasoner
     * @param timeout          The timeout in seconds for the ABA reasoner operation
     * @param answer           The answer/result of the ABA reasoner operation
     * @param time             The execution time of the ABA reasoner operation
     * @param unit_time        The unit time for the ABA reasoner operation
     * @param status           The status of the ABA reasoner response
     */
    public AbaReasonerResponse(String reply, String email, String kb, String kb_format, String fol_signature,
            String query_assumption, String semantics, int timeout, String answer,
            double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.kb = kb;
        this.kb_format = kb_format;
        this.query_assumption = query_assumption;
        this.semantics = semantics;
        this.timeout = timeout;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
        this.fol_signature = fol_signature;
    }

    /**
     * Gets the fol_signature associated with this object.
     *
     * @return The fol_signature.
     */
    public String getFol_signature() {
        return fol_signature;
    }

    /**
     * Sets the fol_signature for this object.
     *
     * @param fol_signature The fol_signature to be set.
     */
    public void setFol_signature(String fol_signature) {
        this.fol_signature = fol_signature;
    }

    /**
     * Gets the status associated with this object.
     *
     * @return The status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status for this object.
     *
     * @param status The status to be set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the reply associated with this object.
     *
     * @return The reply.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply for this object.
     *
     * @param reply The reply to be set.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email associated with this object.
     *
     * @return The email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email for this object.
     *
     * @param email The email to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the kb associated with this object.
     *
     * @return The kb.
     */
    public String getKb() {
        return this.kb;
    }

    /**
     * Sets the knowledge base for this object.
     *
     * @param kb The kb to be set.
     */
    public void setKb(String kb) {
        this.kb = kb;
    }

    /**
     * Gets the kb_format associated with this object.
     *
     * @return The kb_format.
     */
    public String getKb_format() {
        return this.kb_format;
    }

    /**
     * Sets the kb_format for this object.
     *
     * @param kb_format The kb_format to be set.
     */
    public void setKb_format(String kb_format) {
        this.kb_format = kb_format;
    }

    /**
     * Gets the query_assumption associated with this object.
     *
     * @return The query_assumption.
     */
    public String getQuery_assumption() {
        return this.query_assumption;
    }

    /**
     * Sets the query_assumption for this object.
     *
     * @param query_assumption The query_assumption to be set.
     */
    public void setQuery_assumption(String query_assumption) {
        this.query_assumption = query_assumption;
    }

    /**
     * Gets the semantics associated with this object.
     *
     * @return The semantics.
     */
    public String getSemantics() {
        return this.semantics;
    }

    /**
     * Sets the semantics for this object.
     *
     * @param semantics The semantics to be set.
     */
    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    /**
     * Gets the timeout value associated with this object.
     *
     * @return The timeout value.
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Sets the timeout value for this object.
     *
     * @param timeout The timeout value to be set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the answer associated with this object.
     *
     * @return The answer.
     */
    public String getAnswer() {
        return this.answer;
    }

    /**
     * Sets the answer for this object.
     *
     * @param answer The answer to be set.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets the time value associated with this object.
     *
     * @return The time value.
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Sets the time value for this object.
     *
     * @param time The time value to be set.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the unit time associated with this object.
     *
     * @return The unit time.
     */
    public String getUnit_time() {
        return this.unit_time;
    }

    /**
     * Sets the unit time for this object.
     *
     * @param unit_time The unit time to be set.
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * Sets the reply for this object and returns the instance of
     * AbaReasonerResponse.
     *
     * @param reply The reply to be set.
     * @return This AbaReasonerResponse instance.
     */
    public AbaReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email for this object and returns the instance of
     * AbaReasonerResponse.
     *
     * @param email The email to be set.
     * @return This AbaReasonerResponse instance.
     */
    public AbaReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the answer for this object and returns the instance of
     * AbaReasonerResponse.
     *
     * @param answer The answer to be set.
     * @return This AbaReasonerResponse instance.
     */
    public AbaReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * Sets the time for this object and returns the instance of
     * AbaReasonerResponse.
     *
     * @param time The time value to be set.
     * @return This AbaReasonerResponse instance.
     */
    public AbaReasonerResponse time(double time) {
        setTime(time);
        return this;
    }

    /**
     * Sets the unit time for this object and returns the instance of
     * AbaReasonerResponse.
     *
     * @param unit_time The unit time to be set.
     * @return This AbaReasonerResponse instance.
     */
    public AbaReasonerResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * This method compares the fields of two AbaReasonerResponse objects for
     * equality.
     *
     * @param o The reference object with which to compare.
     * @return True if this object is the same as the o argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerResponse)) {
            return false;
        }
        AbaReasonerResponse abaReasonerResponse = (AbaReasonerResponse) o;
        return Objects.equals(reply, abaReasonerResponse.reply) && Objects.equals(email, abaReasonerResponse.email)
                && Objects.equals(kb, abaReasonerResponse.kb)
                && Objects.equals(kb_format, abaReasonerResponse.kb_format)
                && Objects.equals(query_assumption, abaReasonerResponse.query_assumption)
                && Objects.equals(semantics, abaReasonerResponse.semantics) && timeout == abaReasonerResponse.timeout
                && Objects.equals(answer, abaReasonerResponse.answer) && time == abaReasonerResponse.time
                && Objects.equals(unit_time, abaReasonerResponse.unit_time);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(reply, email, kb, kb_format, query_assumption, semantics, timeout, answer, time, unit_time);
    }

    /**
     * Returns a string representation of the object.
     * This method constructs a string representation of the AbaReasonerResponse
     * object
     * including all its fields.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", kb='" + getKb() + "'" +
                ", kb_format='" + getKb_format() + "'" +
                ", query_assumption='" + getQuery_assumption() + "'" +
                ", semantics='" + getSemantics() + "'" +
                ", timeout='" + getTimeout() + "'" +
                ", answer='" + getAnswer() + "'" +
                ", time='" + getTime() + "'" +
                ", unit_time='" + getUnit_time() + "'" +
                "}";
    }

}