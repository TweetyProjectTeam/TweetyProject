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
package org.tweetyproject.web.services.adf;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * The AdfReasonerResponse class extends the Response class and represents
 * a response containing information related to an ADF argumentation reasoner
 * result.
 */
public class AdfReasonerResponse extends Response {

    /** The reply message in the ADF reasoner response */
    private String reply;

    /** The email associated with the ADF reasoner response */
    private String email;

    /** The number of arguments in the ADF reasoner response */
    private int nr_of_arguments;

    /** The conditions information in the ADF reasoner response */
    private List<String> conditions;

    /** The semantics specified in the ADF reasoner response */
    private String semantics;

    /** The solver specified in the ADF reasoner response */
    private String solver;

    /** The answer provided by the ADF reasoner response */
    private String answer;

    /** The time taken for the ADF reasoner operation (in seconds) */
    private double time;

    /** The unit time specified in the ADF reasoner response */
    private String unit_time;

    /** The status of the ADF reasoner response */
    private String status;

    /**
     * Default constructor for ADFReasonerResponse.
     */
    public AdfReasonerResponse() {
    }

    /**
     * Parameterized constructor for ADFReasonerResponse.
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param nr_of_arguments The number of arguments
     * @param conditions      The conditions information
     * @param semantics       The semantics specified
     * @param solver          The solver specified
     * @param answer          The answer provided
     * @param time            The time taken for the operation
     * @param unit_time       The unit time specified
     * @param status          The status of the response
     */
    public AdfReasonerResponse(String reply, String email, int nr_of_arguments, List<String> conditions,
                                String semantics, String solver, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.nr_of_arguments = nr_of_arguments;
        this.conditions = conditions;
        this.semantics = semantics;
        this.solver = solver;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * Gets the reply message in the ADF reasoner response.
     *
     * @return The reply message in the ADF reasoner response.
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply message in the ADF reasoner response.
     *
     * @param reply The reply message in the ADF reasoner response.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email associated with the ADF reasoner response.
     *
     * @return The email associated with the ADF reasoner response.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the ADF reasoner response.
     *
     * @param email The email associated with the ADF reasoner response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the number of arguments in the ADF reasoner response.
     *
     * @return The number of arguments in the ADF reasoner response.
     */
    public int getNr_of_arguments() {
        return nr_of_arguments;
    }

    /**
     * Sets the number of arguments in the ADF reasoner response.
     *
     * @param nr_of_arguments The number of arguments in the ADF reasoner response.
     */
    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    /**
     * Gets the conditions information in the ADF reasoner response.
     *
     * @return The conditions information in the ADF reasoner response.
     */
    public List<String> getConditions() {
        return conditions;
    }

    /**
     * Sets the conditions information in the ADF reasoner response.
     *
     * @param conditions The conditions information in the ADF reasoner response.
     */
    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    /**
     * Gets the semantics specified in the ADF reasoner response.
     *
     * @return The semantics specified in the ADF reasoner response.
     */
    public String getSemantics() {
        return semantics;
    }

    /**
     * Sets the semantics specified in the ADF reasoner response.
     *
     * @param semantics The semantics specified in the ADF reasoner response.
     */
    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    /**
     * Gets the solver specified in the ADF reasoner response.
     *
     * @return The solver specified in the ADF reasoner response.
     */
    public String getSolver() {
        return solver;
    }

    /**
     * Sets the solver specified in the ADF reasoner response.
     *
     * @param solver The solver specified in the ADF reasoner response.
     */
    public void setSolver(String solver) {
        this.solver = solver;
    }

    /**
     * Gets the answer provided by the ADF reasoner response.
     *
     * @return The answer provided by the ADF reasoner response.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer provided by the ADF reasoner response.
     *
     * @param answer The answer provided by the ADF reasoner response.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets the time taken for the ADF reasoner operation (in seconds).
     *
     * @return The time taken for the ADF reasoner operation (in seconds).
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the time taken for the ADF reasoner operation (in seconds).
     *
     * @param time The time taken for the ADF reasoner operation (in seconds).
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the unit time specified in the ADF reasoner response.
     *
     * @return The unit time specified in the ADF reasoner response.
     */
    public String getUnit_time() {
        return unit_time;
    }

    /**
     * Sets the unit time specified in the ADF reasoner response.
     *
     * @param unit_time The unit time specified in the ADF reasoner response.
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * Gets the status of the ADF reasoner response.
     *
     * @return The status of the ADF reasoner response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the ADF reasoner response.
     *
     * @param status The status of the ADF reasoner response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the reply message in the ADF reasoner response.
     *
     * @param reply The reply message in the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email associated with the ADF reasoner response.
     *
     * @param email The email associated with the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the number of arguments in the ADF reasoner response.
     *
     * @param nr_of_arguments The number of arguments in the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse nr_of_arguments(int nr_of_arguments) {
        setNr_of_arguments(nr_of_arguments);
        return this;
    }

    /**
     * Sets the conditions information in the ADF reasoner response.
     *
     * @param conditions The conditions information in the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse conditions(List<String> conditions) {
        setConditions(conditions);
        return this;
    }

    /**
     * Sets the semantics specified in the ADF reasoner response.
     *
     * @param semantics The semantics specified in the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    /**
     * Sets the solver specified in the ADF reasoner response.
     *
     * @param solver The solver specified in the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse solver(String solver) {
        setSolver(solver);
        return this;
    }

    /**
     * Sets the answer provided by the ADF reasoner response.
     *
     * @param answer The answer provided by the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * Sets the time taken for the ADF reasoner operation (in seconds).
     *
     * @param time The time taken for the ADF reasoner operation (in seconds).
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse time(int time) {
        setTime(time);
        return this;
    }

    /**
     * Sets the status of the ADF reasoner response.
     *
     * @param status The status of the ADF reasoner response.
     * @return The current instance of ADFReasonerResponse.
     */
    public AdfReasonerResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AdfReasonerResponse)) {
            return false;
        }
        AdfReasonerResponse tweetyResponse = (AdfReasonerResponse) o;
        return Objects.equals(reply, tweetyResponse.reply) && Objects.equals(email, tweetyResponse.email)
                && nr_of_arguments == tweetyResponse.nr_of_arguments && Objects.equals(conditions, tweetyResponse.conditions)
                && Objects.equals(semantics, tweetyResponse.semantics) && Objects.equals(solver, tweetyResponse.solver)
                && Objects.equals(answer, tweetyResponse.answer) && time == tweetyResponse.time
                && Objects.equals(status, tweetyResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, nr_of_arguments, conditions, semantics, solver, answer, time, status);
    }

    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", nr_of_arguments='" + getNr_of_arguments() + "'" +
                ", conditions='" + getConditions() + "'" +
                ", semantics='" + getSemantics() + "'" +
                ", solver='" + getSolver() + "'" +
                ", answer='" + getAnswer() + "'" +
                ", time='" + getTime() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }

}