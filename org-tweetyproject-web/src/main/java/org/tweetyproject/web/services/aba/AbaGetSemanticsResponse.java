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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * The AbaGetSemanticsResponse class extends the Response class and represents
 * a response containing information about the supported semantics for Assumption
 * Based Argumentation (ABA).
 */
public class AbaGetSemanticsResponse extends Response {

    /** The list of semantics represented as a list of HashMaps */
    private List<HashMap<String, String>> semantics;

    /** The reply message in the AbaGetSemanticsResponse */
    private String reply;

    /** The email associated with the AbaGetSemanticsResponse */
    private String email;

    /**
     * Default constructor for AbaGetSemanticsResponse.
     */
    public AbaGetSemanticsResponse() {
    }

    /**
     * Parameterized constructor for AbaGetSemanticsResponse.
     *
     * @param semantics The list of semantics represented as a list of HashMaps
     * @param reply     The reply message
     * @param email     The email associated with the response
     */
    public AbaGetSemanticsResponse(List<HashMap<String, String>> semantics, String reply, String email) {
        this.semantics = semantics;
        this.reply = reply;
        this.email = email;
    }

    /**
     * Returns the list of semantics.
     *
     * @return A list of HashMaps, each representing a semantic with key-value pairs.
     */
    public List<HashMap<String,String>> getSemantics() {
        return this.semantics;
    }

    /**
     * Sets the list of semantics for this response.
     *
     * @param semantics A list of HashMaps representing semantics.
     */
    public void setSemantics(List<HashMap<String,String>> semantics) {
        this.semantics = semantics;
    }

    /**
     * Retrieves the reply message associated with this response.
     *
     * @return The reply message as a String.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply message for this response.
     *
     * @param reply The reply message to be set.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Retrieves the email address associated with this response.
     *
     * @return The email address as a String.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address for this response.
     *
     * @param email The email address to be associated with this response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method to set the semantics of this response and return the modified object.
     *
     * @param measures A list of HashMaps representing new semantics.
     * @return This AbaGetSemanticsResponse instance for method chaining.
     */
    public AbaGetSemanticsResponse measures(List<HashMap<String,String>> measures) {
        setSemantics(measures);
        return this;
    }

    /**
     * Method to set the reply message for this response and return the modified object.
     *
     * @param reply The new reply message.
     * @return This AbaGetSemanticsResponse instance for method chaining.
     */
    public AbaGetSemanticsResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Method to set the email for this response and return the modified object.
     *
     * @param email The new email address.
     * @return This AbaGetSemanticsResponse instance for method chaining.
     */
    public AbaGetSemanticsResponse email(String email) {
        setEmail(email);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaGetSemanticsResponse)) {
            return false;
        }
        AbaGetSemanticsResponse inconsistencyInfoResponse = (AbaGetSemanticsResponse) o;
        return Objects.equals(semantics, inconsistencyInfoResponse.semantics) && Objects.equals(reply, inconsistencyInfoResponse.reply) && Objects.equals(email, inconsistencyInfoResponse.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(semantics, reply, email);
    }

    @Override
    public String toString() {
        return "{" +
            " measures='" + getSemantics() + "'" +
            ", reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }


}
