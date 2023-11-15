package org.tweetyproject.web.pyargservices.dung;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.pyargservices.Response;

public class DungServicesInfoResponse extends Response {
    private String reply;
    private String email;
    private int backend_timeout;
    private List<String> semantics;
    private List<String> commands;



    public DungServicesInfoResponse() {
    }

    public DungServicesInfoResponse(String reply, String email, int backend_timeout, List<String> semantics, List<String> commands) {
        this.reply = reply;
        this.email = email;
        this.backend_timeout = backend_timeout;
        this.semantics = semantics;
        this.commands = commands;
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

    public int getBackend_timeout() {
        return this.backend_timeout;
    }

    public void setBackend_timeout(int backend_timeout) {
        this.backend_timeout = backend_timeout;
    }

    public List<String> getSemantics() {
        return this.semantics;
    }

    public void setSemantics(List<String> semantics) {
        this.semantics = semantics;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public DungServicesInfoResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public DungServicesInfoResponse email(String email) {
        setEmail(email);
        return this;
    }

    public DungServicesInfoResponse backend_timeout(int backend_timeout) {
        setBackend_timeout(backend_timeout);
        return this;
    }

    public DungServicesInfoResponse semantics(List<String> semantics) {
        setSemantics(semantics);
        return this;
    }

    public DungServicesInfoResponse commands(List<String> commands) {
        setCommands(commands);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DungServicesInfoResponse)) {
            return false;
        }
        DungServicesInfoResponse dungServicesInfoRespones = (DungServicesInfoResponse) o;
        return Objects.equals(reply, dungServicesInfoRespones.reply) && Objects.equals(email, dungServicesInfoRespones.email) && backend_timeout == dungServicesInfoRespones.backend_timeout && Objects.equals(semantics, dungServicesInfoRespones.semantics) && Objects.equals(commands, dungServicesInfoRespones.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, backend_timeout, semantics, commands);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", backend_timeout='" + getBackend_timeout() + "'" +
            ", semantics='" + getSemantics() + "'" +
            ", commands='" + getCommands() + "'" +
            "}";
    }
}
