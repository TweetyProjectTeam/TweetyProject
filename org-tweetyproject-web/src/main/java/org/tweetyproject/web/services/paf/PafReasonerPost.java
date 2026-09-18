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

/**
 * Request payload for the probabilistic argumentation framework (PAF) reasoner endpoint.
 *
 * <p>Arguments are numbered 1..{@code nr_of_arguments}. {@code argument_probabilities[i]}
 * is the existence probability of argument {@code i+1}. {@code attack_probabilities[j]}
 * is the existence probability of {@code attacks[j]}.</p>
 */
public class PafReasonerPost {

    /** Creates an empty request payload. */
    public PafReasonerPost() {
    }

    /** Command to execute (e.g. "get_credulous", "get_skeptical", "info"). */
    private String cmd;

    /** Email of the requesting user. */
    private String email;

    /** Total number of arguments. */
    private int nr_of_arguments;

    /**
     * Existence probability for each argument.
     * Index i corresponds to argument (i+1). Defaults to 1.0 if omitted.
     */
    private List<Double> argument_probabilities;

    /** Attack relations as pairs [attacker, attacked] (1-based indices). */
    private List<List<Integer>> attacks;

    /**
     * Existence probability for each attack.
     * Index j corresponds to {@code attacks[j]}. Defaults to 1.0 if omitted.
     */
    private List<Double> attack_probabilities;

    /** Semantics identifier (e.g. "grounded", "stable", "preferred"). */
    private String semantics;

    /** Solver to use: "simple" (exact) or "montecarlo" (approximate). */
    private String solver;

    /** Number of Monte Carlo trials (only relevant when solver is "montecarlo"). */
    private int nr_of_trials;

    /** 1-based index of the argument to query (used by get_credulous / get_skeptical). */
    private int argument;

    /** Timeout value. */
    private int timeout;

    /** Timeout unit ("sec" or "ms"). */
    private String unit_timeout;

    /**
     * Returns the command to execute.
     *
     * @return command name
     */
    public String getCmd() { return cmd; }

    /**
     * Sets the command to execute.
     *
     * @param cmd command name
     */
    public void setCmd(String cmd) { this.cmd = cmd; }

    /**
     * Returns the user email for the request.
     *
     * @return email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the user email for the request.
     *
     * @param email email address
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the total number of arguments.
     *
     * @return number of arguments
     */
    public int getNr_of_arguments() { return nr_of_arguments; }

    /**
     * Sets the total number of arguments.
     *
     * @param nr_of_arguments number of arguments
     */
    public void setNr_of_arguments(int nr_of_arguments) { this.nr_of_arguments = nr_of_arguments; }

    /**
     * Returns the argument existence probabilities.
     *
     * @return argument probabilities
     */
    public List<Double> getArgument_probabilities() { return argument_probabilities; }

    /**
     * Sets the argument existence probabilities.
     *
     * @param argument_probabilities argument probabilities
     */
    public void setArgument_probabilities(List<Double> argument_probabilities) {
        this.argument_probabilities = argument_probabilities;
    }

    /**
     * Returns the attack relations.
     *
     * @return attack relation list
     */
    public List<List<Integer>> getAttacks() { return attacks; }

    /**
     * Sets the attack relations.
     *
     * @param attacks attack relation list
     */
    public void setAttacks(List<List<Integer>> attacks) { this.attacks = attacks; }

    /**
     * Returns the attack existence probabilities.
     *
     * @return attack probabilities
     */
    public List<Double> getAttack_probabilities() { return attack_probabilities; }

    /**
     * Sets the attack existence probabilities.
     *
     * @param attack_probabilities attack probabilities
     */
    public void setAttack_probabilities(List<Double> attack_probabilities) {
        this.attack_probabilities = attack_probabilities;
    }

    /**
     * Returns the chosen semantics identifier.
     *
     * @return semantics identifier
     */
    public String getSemantics() { return semantics; }

    /**
     * Sets the chosen semantics identifier.
     *
     * @param semantics semantics identifier
     */
    public void setSemantics(String semantics) { this.semantics = semantics; }

    /**
     * Returns the solver name.
     *
     * @return solver name
     */
    public String getSolver() { return solver; }

    /**
     * Sets the solver name.
     *
     * @param solver solver name
     */
    public void setSolver(String solver) { this.solver = solver; }

    /**
     * Returns the number of Monte Carlo trials.
     *
     * @return number of trials
     */
    public int getNr_of_trials() { return nr_of_trials; }

    /**
     * Sets the number of Monte Carlo trials.
     *
     * @param nr_of_trials number of trials
     */
    public void setNr_of_trials(int nr_of_trials) { this.nr_of_trials = nr_of_trials; }

    /**
     * Returns the queried argument index.
     *
     * @return argument index
     */
    public int getArgument() { return argument; }

    /**
     * Sets the queried argument index.
     *
     * @param argument argument index
     */
    public void setArgument(int argument) { this.argument = argument; }

    /**
     * Returns the request timeout value.
     *
     * @return timeout value
     */
    public int getTimeout() { return timeout; }

    /**
     * Sets the request timeout value.
     *
     * @param timeout timeout value
     */
    public void setTimeout(int timeout) { this.timeout = timeout; }

    /**
     * Returns the timeout unit.
     *
     * @return timeout unit
     */
    public String getUnit_timeout() { return unit_timeout; }

    /**
     * Sets the timeout unit.
     *
     * @param unit_timeout timeout unit
     */
    public void setUnit_timeout(String unit_timeout) { this.unit_timeout = unit_timeout; }
}
