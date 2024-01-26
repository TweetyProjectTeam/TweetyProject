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

import org.tweetyproject.web.spring_services.Response;

/**
 * Represents a response object for inconsistency value calculations.
 *
 * The InconsistencyValueResponse class extends the Response class and encapsulates information
 * about the result of an inconsistency value calculation, including the reply status, user email,
 * inconsistency measure, knowledge base, response format, calculated value, execution time, and status.
 *
 * This class provides getter and setter methods for accessing and modifying its attributes.
 * Additionally, convenience methods are included for chaining attribute setting operations.
 * The class overrides the equals, hashCode, and toString methods for proper comparison and string representation.
 *
 * @see Response
 * @see Objects
 */

public class InconsistencyValueResponse extends Response {
    private String reply;
    private String email;
    private String measure;
    private String kb;
    private String format;
    private double value;
    private double time;
    private String status;

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getStatus() {
        return status;
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
     */
    public InconsistencyValueResponse() {
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @param email *description missing*
     * @param measure *description missing*
     * @param kb *description missing*
     * @param format *description missing*
     * @param value *description missing*
     * @param time *description missing*
     */
    public InconsistencyValueResponse(String reply, String email, String measure, String kb, String format, double value, double time) {
        this.reply = reply;
        this.email = email;
        this.measure = measure;
        this.kb = kb;
        this.format = format;
        this.value = value;
        this.time = time;
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
    public String getMeasure() {
        return this.measure;
    }

    /**
     * *description missing*
     * @param measure *description missing*
     */
    public void setMeasure(String measure) {
        this.measure = measure;
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
    public String getFormat() {
        return this.format;
    }

    /**
     * *description missing*
     * @param format *description missing*
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public double getValue() {
        return this.value;
    }

    /**
     * *description missing*
     * @param value *description missing*
     */
    public void setValue(double value) {
        this.value = value;
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
     * @param reply *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * *description missing*
     * @param email *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * *description missing*
     * @param measure *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse measure(String measure) {
        setMeasure(measure);
        return this;
    }

    /**
     * *description missing*
     * @param kb *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * *description missing*
     * @param format *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * *description missing*
     * @param value *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse value(double value) {
        setValue(value);
        return this;
    }

    /**
     * *description missing*
     * @param time *description missing*
     * @return *description missing*
     */
    public InconsistencyValueResponse time(double time) {
        setTime(time);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InconsistencyValueResponse)) {
            return false;
        }
        InconsistencyValueResponse inconsistencyResponse = (InconsistencyValueResponse) o;
        return Objects.equals(reply, inconsistencyResponse.reply) && Objects.equals(email, inconsistencyResponse.email) && Objects.equals(measure, inconsistencyResponse.measure) && Objects.equals(kb, inconsistencyResponse.kb) && Objects.equals(format, inconsistencyResponse.format) && value == inconsistencyResponse.value && time == inconsistencyResponse.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, measure, kb, format, value, time);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", measure='" + getMeasure() + "'" +
            ", kb='" + getKb() + "'" +
            ", format='" + getFormat() + "'" +
            ", value='" + getValue() + "'" +
            ", time='" + getTime() + "'" +
            "}";
    }

}
