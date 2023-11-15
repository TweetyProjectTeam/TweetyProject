package org.tweetyproject.web.pyargservices.incmes;

import java.util.HashMap;
import java.util.List;

import org.tweetyproject.web.pyargservices.Response;

import java.util.Objects;

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
