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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.bipolar;

import org.tweetyproject.web.services.Response;

import java.util.List;

/**
 * The BipolarServicesInfoResponse class extends the Response class and represents
 * a response containing information about bipolar argumentation services.
 */
public class BipolarServicesInfoResponse extends Response {

    /** The reply message in the bipolar services info response */
    private String reply;

    /** The email associated with the bipolar services info response */
    private String email;

    /** The backend timeout value specified in the bipolar services info response */
    private int backend_timeout;

    /** The list of supported semantics in the bipolar services info response */
    private List<String> semantics;

    /** The list of supported commands in the bipolar services info response */
    private List<String> commands;

    /**
     * Default constructor for BipolarServicesInfoResponse .
     */
    public BipolarServicesInfoResponse() {
    }

    /**
     * Parameterized constructor for BipolarServicesInfoResponse .
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param backend_timeout The backend timeout value
     * @param semantics       The list of supported semantics
     * @param commands        The list of supported commands
     */
    public BipolarServicesInfoResponse(String reply, String email, int backend_timeout, List<String> semantics, List<String> commands) {
        this.reply = reply;
        this.email = email;
        this.backend_timeout = backend_timeout;
        this.semantics = semantics;
        this.commands = commands;
    }

    /** 
     * Gets the reply message associated with this response.
     *
     * @return The current reply message as a string.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply message for this response.
     *
     * @param reply The reply message to set.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email address associated with this response.
     *
     * @return The email address as a string.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address for this response.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the backend timeout setting for the service.
     *
     * @return The backend timeout in integer form.
     */
    public int getBackend_timeout() {
        return this.backend_timeout;
    }

    /**
     * Sets the backend timeout for the service.
     *
     * @param backend_timeout The backend timeout value in seconds.
     */
    public void setBackend_timeout(int backend_timeout) {
        this.backend_timeout = backend_timeout;
    }

    /**
     * Gets the list of semantic elements associated with the service.
     *
     * @return A list of semantics as strings.
     */
    public List<String> getSemantics() {
        return this.semantics;
    }

    /**
     * Sets the list of semantic elements for the service.
     *
     * @param semantics A list of semantic strings to be used or defined by the service.
     */
    public void setSemantics(List<String> semantics) {
        this.semantics = semantics;
    }

    /**
     * Gets the list of commands associated with the service.
     *
     * @return A list of commands as strings.
     */
    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * Sets the list of commands for the service.
     *
     * @param commands A list of command strings to be utilized by the service.
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    // Fluent setters

    /**
     * Sets the reply message and returns this instance for method chaining.
     *
     * @param reply The reply message to set.
     * @return This instance to facilitate further modifications.
     */
    public BipolarServicesInfoResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email address and returns this instance for method chaining.
     *
     * @param email The email address to set.
     * @return This instance to facilitate further modifications.
     */
    public BipolarServicesInfoResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the backend timeout and returns this instance for method chaining.
     *
     * @param backend_timeout The backend timeout in seconds to set.
     * @return This instance to facilitate further modifications.
     */
    public BipolarServicesInfoResponse backend_timeout(int backend_timeout) {
        setBackend_timeout(backend_timeout);
        return this;
    }

    /**
     * Sets the semantics and returns this instance for method chaining.
     *
     * @param semantics A list of semantic strings to set.
     * @return This instance to facilitate further modifications.
     */
    public BipolarServicesInfoResponse semantics(List<String> semantics) {
        setSemantics(semantics);
        return this;
    }

    /**
     * Sets the commands and returns this instance for method chaining.
     *
     * @param commands A list of command strings to set.
     * @return This instance to facilitate further modifications.
     */
    public BipolarServicesInfoResponse commands(List<String> commands) {
        setCommands(commands);
        return this;
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
