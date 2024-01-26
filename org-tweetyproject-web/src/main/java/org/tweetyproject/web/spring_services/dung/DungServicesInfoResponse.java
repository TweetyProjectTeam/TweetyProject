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
package org.tweetyproject.web.spring_services.dung;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;


/**
 * The DungServicesInfoResponse class extends the Response class and represents
 * a response containing information about Dung argumentation services.
 */
public class DungServicesInfoResponse extends Response {

    /** The reply message in the Dung services info response */
    private String reply;

    /** The email associated with the Dung services info response */
    private String email;

    /** The backend timeout value specified in the Dung services info response */
    private int backend_timeout;

    /** The list of supported semantics in the Dung services info response */
    private List<String> semantics;

    /** The list of supported commands in the Dung services info response */
    private List<String> commands;

    /**
     * Default constructor for DungServicesInfoResponse.
     */
    public DungServicesInfoResponse() {
    }

    /**
     * Parameterized constructor for DungServicesInfoResponse.
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param backend_timeout The backend timeout value
     * @param semantics       The list of supported semantics
     * @param commands        The list of supported commands
     */
    public DungServicesInfoResponse(String reply, String email, int backend_timeout, List<String> semantics, List<String> commands) {
        this.reply = reply;
        this.email = email;
        this.backend_timeout = backend_timeout;
        this.semantics = semantics;
        this.commands = commands;
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
    public int getBackend_timeout() {
        return this.backend_timeout;
    }

    /**
     * *description missing*
     * @param backend_timeout *description missing*
     */
    public void setBackend_timeout(int backend_timeout) {
        this.backend_timeout = backend_timeout;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public List<String> getSemantics() {
        return this.semantics;
    }

    /**
     * *description missing*
     * @param semantics *description missing*
     */
    public void setSemantics(List<String> semantics) {
        this.semantics = semantics;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * *description missing*
     * @param commands *description missing*
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @return *description missing*
     */
    public DungServicesInfoResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * *description missing*
     * @param email *description missing*
     * @return *description missing*
     */
    public DungServicesInfoResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * *description missing*
     * @param backend_timeout *description missing*
     * @return *description missing*
     */
    public DungServicesInfoResponse backend_timeout(int backend_timeout) {
        setBackend_timeout(backend_timeout);
        return this;
    }

    /**
     * *description missing*
     * @param semantics *description missing*
     * @return *description missing*
     */
    public DungServicesInfoResponse semantics(List<String> semantics) {
        setSemantics(semantics);
        return this;
    }

    /**
     * *description missing*
     * @param commands *description missing*
     * @return *description missing*
     */
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
