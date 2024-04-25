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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;


/**
 * Represents a response for inconsistency measures retrieval requests.
 *
 * The InconsistencyGetMeasuresResponse class extends the Response class and encapsulates information
 * about the measures retrieved, reply status, and user email associated with the request.
 *
 * This class provides getter and setter methods for accessing and modifying its attributes.
 * Additionally, convenience methods are included for chaining attribute setting operations.
 * The class overrides the equals, hashCode, and toString methods for proper comparison and string representation.
 *
 * @see Response
 * @see Objects
 */

/**
 * Represents a response object that contains a list of measures related to identified inconsistencies.
 * This class extends {@link Response} to inherit base response functionality.
 */
public class InconsistencyGetMeasuresResponse extends Response {
    private List<HashMap<String, String>> measures;
    private String reply;
    private String email;

    /**
     * Constructs an empty {@code InconsistencyGetMeasuresResponse} with uninitialized fields.
     * This default constructor is useful when the object needs to be populated manually.
     */
    public InconsistencyGetMeasuresResponse() {
    }

    /**
     * Constructs a {@code InconsistencyGetMeasuresResponse} initialized with measures, a reply message, and an email.
     *
     * @param measures A list of hashmaps, each representing a measure and its related data.
     * @param reply A string containing a response or feedback related to the request.
     * @param email The email address associated with the request for tracking or response purposes.
     */
    public InconsistencyGetMeasuresResponse(List<HashMap<String,String>> measures, String reply, String email) {
        this.measures = measures;
        this.reply = reply;
        this.email = email;
    }

    /**
     * Retrieves the list of measures related to the inconsistency analysis.
     *
     * @return A list of hashmaps where each hashmap contains key-value pairs representing measure attributes.
     */
    public List<HashMap<String,String>> getMeasures() {
        return this.measures;
    }

    /**
     * Sets the list of measures associated with the inconsistency analysis.
     *
     * @param measures A list of hashmaps where each hashmap includes measure-specific data.
     */
    public void setMeasures(List<HashMap<String,String>> measures) {
        this.measures = measures;
    }

    /**
     * Retrieves the reply message associated with this response.
     *
     * @return A string containing the reply message.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply message for this response.
     *
     * @param reply A string that will be used as the reply or feedback.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Retrieves the email address associated with this response.
     *
     * @return A string containing the email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address for this response.
     *
     * @param email A string containing the email address to be associated with this response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method to set the measures and return the modified response object.
     *
     * @param measures A list of hashmaps with measure-related data to set.
     * @return This {@code InconsistencyGetMeasuresResponse} instance to facilitate further modifications.
     */
    public InconsistencyGetMeasuresResponse measures(List<HashMap<String,String>> measures) {
        setMeasures(measures);
        return this;
    }

    /**
     * Method to set the reply and return the modified response object.
     *
     * @param reply A string to set as the reply or feedback.
     * @return This {@code InconsistencyGetMeasuresResponse} instance to facilitate further modifications.
     */
    public InconsistencyGetMeasuresResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Method to set the email and return the modified response object.
     *
     * @param email A string to set as the email address.
     * @return This {@code InconsistencyGetMeasuresResponse} instance to facilitate further modifications.
     */
    public InconsistencyGetMeasuresResponse email(String email) {
        setEmail(email);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InconsistencyGetMeasuresResponse)) {
            return false;
        }
        InconsistencyGetMeasuresResponse inconsistencyInfoResponse = (InconsistencyGetMeasuresResponse) o;
        return Objects.equals(measures, inconsistencyInfoResponse.measures) && Objects.equals(reply, inconsistencyInfoResponse.reply) && Objects.equals(email, inconsistencyInfoResponse.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measures, reply, email);
    }

    @Override
    public String toString() {
        return "{" +
            " measures='" + getMeasures() + "'" +
            ", reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }


}
