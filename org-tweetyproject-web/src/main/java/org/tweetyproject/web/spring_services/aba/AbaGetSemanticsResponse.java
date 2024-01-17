package org.tweetyproject.web.spring_services.aba;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;

public class AbaGetSemanticsResponse extends Response {
    private List<HashMap<String, String>> semantics;
    private String reply;
    private String email;

    public AbaGetSemanticsResponse() {
    }

    public AbaGetSemanticsResponse(List<HashMap<String,String>> measures, String reply, String email) {
        this.semantics = measures;
        this.reply = reply;
        this.email = email;
    }

    public List<HashMap<String,String>> getSemantics() {
        return this.semantics;
    }

    public void setSemantics(List<HashMap<String,String>> measures) {
        this.semantics = measures;
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

    public AbaGetSemanticsResponse measures(List<HashMap<String,String>> measures) {
        setSemantics(measures);
        return this;
    }

    public AbaGetSemanticsResponse reply(String reply) {
        setReply(reply);
        return this;
    }

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
