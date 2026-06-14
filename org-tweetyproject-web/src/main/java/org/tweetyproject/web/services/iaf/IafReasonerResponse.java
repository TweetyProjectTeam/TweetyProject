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
package org.tweetyproject.web.services.iaf;

import org.tweetyproject.web.services.Response;

import java.util.List;
import java.util.Objects;

/**
 * The iAFReasonerResponse class extends the Response class and represents
 * a response containing information related to a iAF argumentation reasoner
 * result.
 */
public class IafReasonerResponse extends Response {

    /** The reply message in the iAF reasoner response */
    private String reply;

    /** The email associated with the iAF reasoner response */
    private String email;

    /** The number of arguments in the iAF reasoner response */
    private int nr_of_arguments;
    
    /** The uncertain arguments information in the iAF reasoner response */
    private List<Integer> uncertainArguments;

    /** The definite attacks information in the iAF reasoner response */
    private List<List<Integer>> definiteAttacks;

    /** The uncertain attacks information in the iAF reasoner response */
    private List<List<Integer>> uncertainAttacks;

    /** The semantics specified in the iAF reasoner response */
    private String semantics;

    /** The solver specified in the iAF reasoner response */
    private String solver;

    /** The answer provided by the iAF reasoner response */
    private String answer;

    /** The time taken for the iAF reasoner operation (in seconds) */
    private double time;

    /** The unit time specified in the iAF reasoner response */
    private String unit_time;

    /** The status of the iAF reasoner response */
    private String status;

    /**
     * Default constructor for iAFReasonerResponse.
     */
    public IafReasonerResponse() {
    }

    /**
     * Parameterized constructor for iAFReasonerResponse.
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param nr_of_arguments The number of arguments
     * @param uncertainArguments The uncertain arguments
     * @param definiteAttacks    The definite attacks information
     * @param uncertainAttacks   The uncertain attacks information
     * @param semantics       The semantics specified
     * @param solver          The solver specified
     * @param answer          The answer provided
     * @param time            The time taken for the operation
     * @param unit_time       The unit time specified
     * @param status          The status of the response
     */
    public IafReasonerResponse(String reply, String email, int nr_of_arguments, List<Integer> uncertainArguments, List<List<Integer>> definiteAttacks, List<List<Integer>> uncertainAttacks,
                               String semantics, String solver, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.nr_of_arguments = nr_of_arguments;
        this.uncertainArguments = uncertainArguments;
        this.definiteAttacks = definiteAttacks;
        this.uncertainAttacks = uncertainAttacks;
        this.semantics = semantics;
        this.solver = solver;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * Gets the reply message in the iAF reasoner response.
     *
     * @return The reply message in the iAF reasoner response.
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply message in the iAF reasoner response.
     *
     * @param reply The reply message in the iAF reasoner response.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email associated with the iAF reasoner response.
     *
     * @return The email associated with the iAF reasoner response.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the iAF reasoner response.
     *
     * @param email The email associated with the iAF reasoner response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the number of arguments in the iAF reasoner response.
     *
     * @return The number of arguments in the iAF reasoner response.
     */
    public int getNr_of_arguments() {
        return nr_of_arguments;
    }

    /**
     * Sets the number of arguments in the iAF reasoner response.
     *
     * @param nr_of_arguments The number of arguments in the iAF reasoner response.
     */
    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    /**
     * Gets the uncertain arguments information in the iAF reasoner response.
     *
     * @return The uncertain arguments information
     */
    public List<Integer> getUncertainArguments() {
        return this.uncertainArguments;
    }

    /**
     * Sets the uncertain arguments information in the iAF reasoner response.
     *
     * @param uncertainArguments The uncertain arguments information to be set
     */
    public void setUncertainArguments(List<Integer> uncertainArguments) {
        this.uncertainArguments = uncertainArguments;
    }

    /**
     * Gets the definite attacks information in the iAF reasoner response.
     *
     * @return The definite attacks information
     */
    public List<List<Integer>> getDefiniteAttacks() {
        return this.definiteAttacks;
    }

    /**
     * Sets the uncertain attacks information in the iAF reasoner response.
     *
     * @param uncertainAttacks The uncertain attacks information to be set
     */
    public void setUncertainAttacks(List<List<Integer>> uncertainAttacks) {
        this.uncertainAttacks = uncertainAttacks;
    }

    /**
     * Gets the uncertain attacks information in the iAF reasoner response.
     *
     * @return The uncertain attacks information
     */
    public List<List<Integer>> getUncertainAttacks() {
        return this.uncertainAttacks;
    }

    /**
     * Sets the definite attacks information in the iAF reasoner response.
     *
     * @param definiteAttacks The definite attacks information to be set
     */
    public void setDefiniteAttacks(List<List<Integer>> definiteAttacks) {
        this.definiteAttacks = definiteAttacks;
    }


    /**
     * Gets the semantics specified in the iAF reasoner response.
     *
     * @return The semantics specified in the iAF reasoner response.
     */
    public String getSemantics() {
        return semantics;
    }

    /**
     * Sets the semantics specified in the iAF reasoner response.
     *
     * @param semantics The semantics specified in the iAF reasoner response.
     */
    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    /**
     * Gets the solver specified in the iAF reasoner response.
     *
     * @return The solver specified in the iAF reasoner response.
     */
    public String getSolver() {
        return solver;
    }

    /**
     * Sets the solver specified in the iAF reasoner response.
     *
     * @param solver The solver specified in the iAF reasoner response.
     */
    public void setSolver(String solver) {
        this.solver = solver;
    }

    /**
     * Gets the answer provided by the iAF reasoner response.
     *
     * @return The answer provided by the iAF reasoner response.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer provided by the iAF reasoner response.
     *
     * @param answer The answer provided by the iAF reasoner response.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets the time taken for the iAF reasoner operation (in seconds).
     *
     * @return The time taken for the iAF reasoner operation (in seconds).
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the time taken for the iAF reasoner operation (in seconds).
     *
     * @param time The time taken for the iAF reasoner operation (in seconds).
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the unit time specified in the iAF reasoner response.
     *
     * @return The unit time specified in the iAF reasoner response.
     */
    public String getUnit_time() {
        return unit_time;
    }

    /**
     * Sets the unit time specified in the iAF reasoner response.
     *
     * @param unit_time The unit time specified in the iAF reasoner response.
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * Gets the status of the iAF reasoner response.
     *
     * @return The status of the iAF reasoner response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the iAF reasoner response.
     *
     * @param status The status of the iAF reasoner response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the reply message in the iAF reasoner response.
     *
     * @param reply The reply message in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email associated with the iAF reasoner response.
     *
     * @param email The email associated with the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the number of arguments in the iAF reasoner response.
     *
     * @param nr_of_arguments The number of arguments in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse nr_of_arguments(int nr_of_arguments) {
        setNr_of_arguments(nr_of_arguments);
        return this;
    }

    /**
     * Sets the uncertain arguments information in the iAF reasoner response.
     *
     * @param arguments The uncertain arguments information in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse uncertainArguments(List<Integer> arguments) {
        setUncertainArguments(arguments);
        return this;
    }

    /**
     * Sets the definite attacks information in the iAF reasoner response.
     *
     * @param attacks The definite attacks information in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse definiteAttacks(List<List<Integer>> attacks) {
        setDefiniteAttacks(attacks);
        return this;
    }

    /**
     * Sets the uncertain attacks information in the iAF reasoner response.
     *
     * @param attacks The uncertain attacks information in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse uncertainAttacks(List<List<Integer>> attacks) {
        setUncertainAttacks(attacks);
        return this;
    }

    /**
     * Sets the semantics specified in the iAF reasoner response.
     *
     * @param semantics The semantics specified in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    /**
     * Sets the solver specified in the iAF reasoner response.
     *
     * @param solver The solver specified in the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse solver(String solver) {
        setSolver(solver);
        return this;
    }

    /**
     * Sets the answer provided by the iAF reasoner response.
     *
     * @param answer The answer provided by the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * Sets the time taken for the iAF reasoner operation (in seconds).
     *
     * @param time The time taken for the iAF reasoner operation (in seconds).
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse time(int time) {
        setTime(time);
        return this;
    }

    /**
     * Sets the status of the iAF reasoner response.
     *
     * @param status The status of the iAF reasoner response.
     * @return The current instance of iAFReasonerResponse.
     */
    public IafReasonerResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof IafReasonerResponse)) {
            return false;
        }
        IafReasonerResponse tweetyResponse = (IafReasonerResponse) o;
        return Objects.equals(reply, tweetyResponse.reply) && Objects.equals(email, tweetyResponse.email)
                && nr_of_arguments == tweetyResponse.nr_of_arguments && Objects.equals(uncertainArguments, tweetyResponse.uncertainArguments)
                && Objects.equals(definiteAttacks, tweetyResponse.definiteAttacks) && Objects.equals(uncertainAttacks, tweetyResponse.uncertainAttacks)
                && Objects.equals(semantics, tweetyResponse.semantics) && Objects.equals(solver, tweetyResponse.solver)
                && Objects.equals(answer, tweetyResponse.answer) && time == tweetyResponse.time
                && Objects.equals(status, tweetyResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, nr_of_arguments, uncertainArguments, definiteAttacks, uncertainAttacks, semantics, solver, answer, time, status);
    }

    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", nr_of_arguments='" + getNr_of_arguments() + "'" +
                ", uncertainArguments='" + getUncertainArguments() + "'" +
                ", definiteAttacks='" + getDefiniteAttacks() + "'" +
                ", uncertainAttacks='" + getUncertainAttacks() + "'" +
                ", semantics='" + getSemantics() + "'" +
                ", solver='" + getSolver() + "'" +
                ", answer='" + getAnswer() + "'" +
                ", time='" + getTime() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }

}