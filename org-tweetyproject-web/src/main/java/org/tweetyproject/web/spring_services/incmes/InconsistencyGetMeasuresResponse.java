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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;


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

public class InconsistencyGetMeasuresResponse extends Response {
    private List<HashMap<String, String>> measures;
    private String reply;
    private String email;

    public InconsistencyGetMeasuresResponse() {
    }

    public InconsistencyGetMeasuresResponse(List<HashMap<String,String>> measures, String reply, String email) {
        this.measures = measures;
        this.reply = reply;
        this.email = email;
    }

    public List<HashMap<String,String>> getMeasures() {
        return this.measures;
    }

    public void setMeasures(List<HashMap<String,String>> measures) {
        this.measures = measures;
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

    public InconsistencyGetMeasuresResponse measures(List<HashMap<String,String>> measures) {
        setMeasures(measures);
        return this;
    }

    public InconsistencyGetMeasuresResponse reply(String reply) {
        setReply(reply);
        return this;
    }

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
