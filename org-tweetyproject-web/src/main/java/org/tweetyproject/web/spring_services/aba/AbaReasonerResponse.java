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
package org.tweetyproject.web.spring_services.aba;

import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;
/**
 * The AbaReasonerResponse class extends the Response class and represents the response
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

        public String getFol_signature() {
          return fol_signature;
        }

        public void setFol_signature(String fol_signature) {
          this.fol_signature = fol_signature;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    public String getReply() {
        return this.reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getKb_format() {
        return this.kb_format;
    }

    public void setKb_format(String kb_format) {
        this.kb_format = kb_format;
    }

    public String getQuery_assumption() {
        return this.query_assumption;
    }

    public void setQuery_assumption(String query_assumption) {
        this.query_assumption = query_assumption;
    }

    public String getSemantics() {
        return this.semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getUnit_time() {
        return this.unit_time;
    }

    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    public AbaReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public AbaReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    public AbaReasonerResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    public AbaReasonerResponse kb_format(String kb_format) {
        setKb_format(kb_format);
        return this;
    }

    public AbaReasonerResponse query_assumption(String query_assumption) {
        setQuery_assumption(query_assumption);
        return this;
    }

    public AbaReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    public AbaReasonerResponse timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    public AbaReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    public AbaReasonerResponse time(double time) {
        setTime(time);
        return this;
    }

    public AbaReasonerResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerResponse)) {
            return false;
        }
        AbaReasonerResponse abaReasonerResponse = (AbaReasonerResponse) o;
        return Objects.equals(reply, abaReasonerResponse.reply) && Objects.equals(email, abaReasonerResponse.email) && Objects.equals(kb, abaReasonerResponse.kb) && Objects.equals(kb_format, abaReasonerResponse.kb_format) && Objects.equals(query_assumption, abaReasonerResponse.query_assumption) && Objects.equals(semantics, abaReasonerResponse.semantics) && timeout == abaReasonerResponse.timeout && Objects.equals(answer, abaReasonerResponse.answer) && time == abaReasonerResponse.time && Objects.equals(unit_time, abaReasonerResponse.unit_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, kb, kb_format, query_assumption, semantics, timeout, answer, time, unit_time);
    }

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