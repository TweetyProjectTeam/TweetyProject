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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;

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

    public List<HashMap<String,String>> getSemantics() {
        return this.semantics;
    }

    /**
     * *description missing*
     * @param measures *description missing*
     */
    public void setSemantics(List<HashMap<String,String>> measures) {
        this.semantics = measures;
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
     * @param measures *description missing*
     * @return *description missing*
     */
    public AbaGetSemanticsResponse measures(List<HashMap<String,String>> measures) {
        setSemantics(measures);
        return this;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @return *description missing*
     */
    public AbaGetSemanticsResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * *description missing*
     * @param email *description missing*
     * @return *description missing*
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
