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

import org.tweetyproject.web.services.Response;

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


    public InconsistencyValueResponse() {
    }
    
    /**
     * Constructs a fully initialized {@code InconsistencyValueResponse} with the specified details.
     *
     * @param reply A message or feedback related to the inconsistency.
     * @param email The email address associated with the request or context.
     * @param measure The specific measure or metric where inconsistency was noted.
     * @param kb Knowledge base reference or identifier used in the assessment.
     * @param format The data format or structure that was analyzed.
     * @param value The numeric value indicating the level of inconsistency.
     * @param time The duration or time taken to evaluate the inconsistency.
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
     * Gets the current status of the response.
     *
     * @return The current status string which could indicate completion, error, or any other state of the response.
     */
    public String getStatus() {
        return status;
    }

        

    /**
     * Sets the status for this response.
     *
     * @param status The string to be used as the status in the response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the reply associated with this response.
     *
     * @return The reply string provided in the response.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply for this response.
     *
     * @param reply The text to be used as the reply in the response.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

        /**
     * Retrieves the email address associated with the inconsistency report or request.
     *
     * @return The email address as a string.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address associated with the inconsistency report or request.
     *
     * @param email A string representing the email address to be associated with the report.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the specific measure or metric related to the inconsistency detected.
     *
     * @return A string representing the measure or metric where the inconsistency was found.
     */
    public String getMeasure() {
        return this.measure;
    }

    /**
     * Sets the specific measure or metric related to the inconsistency detected.
     *
     * @param measure A string that specifies the measure or metric being addressed.
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * Gets the knowledge base reference or identifier that was used during the evaluation of inconsistency.
     *
     * @return A string representing the knowledge base reference or identifier.
     */
    public String getKb() {
        return this.kb;
    }

    /**
     * Sets the knowledge base reference or identifier used during the evaluation.
     *
     * @param kb A string representing the knowledge base reference or identifier to be used for further evaluation.
     */
    public void setKb(String kb) {
        this.kb = kb;
    }


    /**
     * Retrieves the data format used in the inconsistency evaluation.
     *
     * @return The data format as a string, which describes the structure or type of data analyzed.
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Sets the data format used in the inconsistency evaluation.
     *
     * @param format A string representing the format to be used or that was used in analyzing the data.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Retrieves the numeric value indicating the level of inconsistency found.
     *
     * @return A double value representing the degree of inconsistency.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the numeric value indicating the level of inconsistency.
     *
     * @param value A double representing the new value of inconsistency to be recorded.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Retrieves the duration or time taken to analyze the inconsistency.
     *
     * @return A double value representing the time in some units (e.g., milliseconds, seconds) taken to perform the analysis.
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Sets the duration or time taken to analyze the inconsistency.
     *
     * @param time A double value representing the time in units taken for the inconsistency analysis.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Sets the reply property of this response and returns the response object itself for method chaining.
     *
     * @param reply A string containing the reply or feedback related to the inconsistency.
     * @return This {@code InconsistencyValueResponse} instance for chaining method calls.
     */
    public InconsistencyValueResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email property of this response and returns the response object itself for method chaining.
     *
     * @param email A string containing the email address associated with the inconsistency report.
     * @return This {@code InconsistencyValueResponse} instance for chaining method calls.
     */
    public InconsistencyValueResponse email(String email) {
        setEmail(email);
        return this;
    }


        /**
     * Sets the measure or metric related to the inconsistency and returns this response object to facilitate method chaining.
     *
     * @param measure A string specifying the measure or metric where the inconsistency was detected.
     * @return This {@code InconsistencyValueResponse} instance for chaining further method calls.
     */
    public InconsistencyValueResponse measure(String measure) {
        setMeasure(measure);
        return this;
    }

    /**
     * Sets the knowledge base (kb) reference or identifier used during the evaluation of inconsistency and returns this response object for method chaining.
     *
     * @param kb A string representing the knowledge base reference or identifier.
     * @return This {@code InconsistencyValueResponse} instance for chaining further method calls.
     */
    public InconsistencyValueResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    /**
     * Sets the data format used in the inconsistency evaluation and returns this response object for method chaining.
     *
     * @param format A string that describes the data format or structure analyzed.
     * @return This {@code InconsistencyValueResponse} instance for chaining further method calls.
     */
    public InconsistencyValueResponse format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Sets the numeric value indicating the level of inconsistency detected and returns this response object to facilitate method chaining.
     *
     * @param value A double representing the degree of inconsistency measured.
     * @return This {@code InconsistencyValueResponse} instance for chaining further method calls.
     */
    public InconsistencyValueResponse value(double value) {
        setValue(value);
        return this;
    }

    /**
     * Sets the duration or time taken to evaluate the inconsistency and returns this response object for method chaining.
     *
     * @param time A double value representing the time in some unit (e.g., seconds, milliseconds) taken to perform the analysis.
     * @return This {@code InconsistencyValueResponse} instance for chaining further method calls.
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
