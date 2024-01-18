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
package org.tweetyproject.web.spring_services.delp;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;
/**
 * *description missing*
 */
public class DelpResponse extends Response {
    private String reply;
    private String email;
    private String compcriterion;
    private String kb;
    private String query;
    private int timeout;
    private String answer;
    private double time;
    private String unit_time;
    private String status;


    /**
     * *description missing*
     */
    public DelpResponse() {
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @param email *description missing*
     * @param compcriterion *description missing*
     * @param kb *description missing*
     * @param query *description missing*
     * @param timeout *description missing*
     * @param answer *description missing*
     * @param time *description missing*
     * @param unit_time *description missing*
     * @param status *description missing*
     */
    public DelpResponse(String reply, String email, String compcriterion, String kb, String query, int timeout, String answer, double time, String unit_time, String status) {
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
     * *description missing*
     * @return *description missing*
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     */
    public void setReply(String reply) {
        this.reply = reply;
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
    public String getAnswer() {
        return this.answer;
    }

    /**
     * *description missing*
     * @param answer *description missing*
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public double getTime() {
        return this.time;
    }

    /**
     * *description missing*
     * @param time *description missing*
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getUnit_time() {
        return this.unit_time;
    }

    /**
     * *description missing*
     * @param unit_time *description missing*
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * *description missing*
     * @param status *description missing*
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @return *description missing*
     */
    public DelpResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * *description missing*
     * @param email *description missing*
     * @return *description missing*
     */
    public DelpResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * *description missing*
     * @param compcriterion *description missing*
     * @return *description missing*
     */
    public DelpResponse compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    /**
     * *description missing*
     * @param kb *description missing*
     * @return *description missing*
     */
    public DelpResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * *description missing*
     * @param query *description missing*
     * @return *description missing*
     */
    public DelpResponse query(String query) {
        setQuery(query);
        return this;
    }

    /**
     * *description missing*
     * @param timeout *description missing*
     * @return *description missing*
     */
    public DelpResponse timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    /**
     * *description missing*
     * @param answer *description missing*
     * @return *description missing*
     */
    public DelpResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * *description missing*
     * @param time *description missing*
     * @return *description missing*
     */
    public DelpResponse time(double time) {
        setTime(time);
        return this;
    }

    /**
     * *description missing*
     * @param unit_time *description missing*
     * @return *description missing*
     */
    public DelpResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    /**
     * *description missing*
     * @param status *description missing*
     * @return *description missing*
     */
    public DelpResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DelpResponse)) {
            return false;
        }
        DelpResponse delpResponse = (DelpResponse) o;
        return Objects.equals(reply, delpResponse.reply) && Objects.equals(email, delpResponse.email) && Objects.equals(compcriterion, delpResponse.compcriterion) && Objects.equals(kb, delpResponse.kb) && Objects.equals(query, delpResponse.query) && timeout == delpResponse.timeout && Objects.equals(answer, delpResponse.answer) && time == delpResponse.time && Objects.equals(unit_time, delpResponse.unit_time) && Objects.equals(status, delpResponse.status);
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