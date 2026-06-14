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
package org.tweetyproject.web.services.paf;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * Response payload for the probabilistic argumentation framework (PAF) reasoner endpoint.
 */
public class PafReasonerResponse extends Response {

    private String reply;
    private String email;
    private int nr_of_arguments;
    private List<Double> argument_probabilities;
    private List<List<Integer>> attacks;
    private List<Double> attack_probabilities;
    private String semantics;
    private String solver;
    private int argument;
    private String answer;
    private double time;
    private String unit_time;
    private String status;

    /**
     * Creates an empty PAF reasoner response.
     */
    public PafReasonerResponse() {}

    /**
     * Creates a PAF reasoner response with all response fields.
     *
     * @param reply                reply message
     * @param email                contact email
     * @param nr_of_arguments      number of arguments in the PAF
     * @param argument_probabilities probabilities for each argument
     * @param attacks              attack relation list
     * @param attack_probabilities attack probabilities corresponding to attacks
     * @param semantics            semantics used by the reasoner
     * @param solver               solver used by the reasoner
     * @param argument             queried argument index
     * @param answer               result answer value
     * @param time                 computation time
     * @param unit_time            time unit for the computation time
     * @param status               status of the reasoner response
     */
    public PafReasonerResponse(String reply, String email, int nr_of_arguments,
            List<Double> argument_probabilities, List<List<Integer>> attacks,
            List<Double> attack_probabilities, String semantics, String solver,
            int argument, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.nr_of_arguments = nr_of_arguments;
        this.argument_probabilities = argument_probabilities;
        this.attacks = attacks;
        this.attack_probabilities = attack_probabilities;
        this.semantics = semantics;
        this.solver = solver;
        this.argument = argument;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * Returns the reply message.
     *
     * @return reply message
     */
    public String getReply() { return reply; }

    /**
     * Sets the reply message.
     *
     * @param reply reply message
     */
    public void setReply(String reply) { this.reply = reply; }

    /**
     * Returns the contact email.
     *
     * @return contact email
     */
    public String getEmail() { return email; }

    /**
     * Sets the contact email.
     *
     * @param email contact email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the number of arguments in the PAF.
     *
     * @return number of arguments
     */
    public int getNr_of_arguments() { return nr_of_arguments; }

    /**
     * Sets the number of arguments in the PAF.
     *
     * @param nr_of_arguments number of arguments
     */
    public void setNr_of_arguments(int nr_of_arguments) { this.nr_of_arguments = nr_of_arguments; }

    /**
     * Returns the computed argument probabilities.
     *
     * @return argument probabilities
     */
    public List<Double> getArgument_probabilities() { return argument_probabilities; }

    /**
     * Sets the computed argument probabilities.
     *
     * @param argument_probabilities argument probabilities
     */
    public void setArgument_probabilities(List<Double> argument_probabilities) {
        this.argument_probabilities = argument_probabilities;
    }

    /**
     * Returns the attack relations.
     *
     * @return attack relations
     */
    public List<List<Integer>> getAttacks() { return attacks; }

    /**
     * Sets the attack relations.
     *
     * @param attacks attack relations
     */
    public void setAttacks(List<List<Integer>> attacks) { this.attacks = attacks; }

    /**
     * Returns the attack probabilities.
     *
     * @return attack probabilities
     */
    public List<Double> getAttack_probabilities() { return attack_probabilities; }

    /**
     * Sets the attack probabilities.
     *
     * @param attack_probabilities attack probabilities
     */
    public void setAttack_probabilities(List<Double> attack_probabilities) {
        this.attack_probabilities = attack_probabilities;
    }

    /**
     * Returns the semantics used by the reasoner.
     *
     * @return semantics
     */
    public String getSemantics() { return semantics; }

    /**
     * Sets the semantics used by the reasoner.
     *
     * @param semantics semantics
     */
    public void setSemantics(String semantics) { this.semantics = semantics; }

    /**
     * Returns the solver used by the reasoner.
     *
     * @return solver
     */
    public String getSolver() { return solver; }

    /**
     * Sets the solver used by the reasoner.
     *
     * @param solver solver
     */
    public void setSolver(String solver) { this.solver = solver; }

    /**
     * Returns the queried argument index.
     *
     * @return queried argument index
     */
    public int getArgument() { return argument; }

    /**
     * Sets the queried argument index.
     *
     * @param argument queried argument index
     */
    public void setArgument(int argument) { this.argument = argument; }

    /**
     * Returns the answer string.
     *
     * @return answer string
     */
    public String getAnswer() { return answer; }

    /**
     * Sets the answer string.
     *
     * @param answer answer string
     */
    public void setAnswer(String answer) { this.answer = answer; }

    /**
     * Returns the computation time.
     *
     * @return computation time
     */
    public double getTime() { return time; }

    /**
     * Sets the computation time.
     *
     * @param time computation time
     */
    public void setTime(double time) { this.time = time; }

    /**
     * Returns the time unit for the computation time.
     *
     * @return time unit
     */
    public String getUnit_time() { return unit_time; }

    /**
     * Sets the time unit for the computation time.
     *
     * @param unit_time time unit
     */
    public void setUnit_time(String unit_time) { this.unit_time = unit_time; }

    /**
     * Returns the response status.
     *
     * @return response status
     */
    public String getStatus() { return status; }

    /**
     * Sets the response status.
     *
     * @param status response status
     */
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PafReasonerResponse)) return false;
        PafReasonerResponse r = (PafReasonerResponse) o;
        return Objects.equals(reply, r.reply) && Objects.equals(email, r.email)
                && nr_of_arguments == r.nr_of_arguments && Objects.equals(semantics, r.semantics)
                && Objects.equals(solver, r.solver) && argument == r.argument
                && Objects.equals(answer, r.answer) && time == r.time
                && Objects.equals(status, r.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, nr_of_arguments, semantics, solver, argument, answer, time, status);
    }

    @Override
    public String toString() {
        return "{reply='" + reply + "', email='" + email + "', nr_of_arguments=" + nr_of_arguments
                + ", semantics='" + semantics + "', solver='" + solver + "', argument=" + argument
                + ", answer='" + answer + "', time=" + time + ", status='" + status + "'}";
    }
}
