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

    public String getCmd() { return cmd; }
    public void setCmd(String cmd) { this.cmd = cmd; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getNr_of_arguments() { return nr_of_arguments; }
    public void setNr_of_arguments(int nr_of_arguments) { this.nr_of_arguments = nr_of_arguments; }

    public List<Double> getArgument_probabilities() { return argument_probabilities; }
    public void setArgument_probabilities(List<Double> argument_probabilities) {
        this.argument_probabilities = argument_probabilities;
    }

    public List<List<Integer>> getAttacks() { return attacks; }
    public void setAttacks(List<List<Integer>> attacks) { this.attacks = attacks; }

    public List<Double> getAttack_probabilities() { return attack_probabilities; }
    public void setAttack_probabilities(List<Double> attack_probabilities) {
        this.attack_probabilities = attack_probabilities;
    }

    public String getSemantics() { return semantics; }
    public void setSemantics(String semantics) { this.semantics = semantics; }

    public String getSolver() { return solver; }
    public void setSolver(String solver) { this.solver = solver; }

    public int getNr_of_trials() { return nr_of_trials; }
    public void setNr_of_trials(int nr_of_trials) { this.nr_of_trials = nr_of_trials; }

    public int getArgument() { return argument; }
    public void setArgument(int argument) { this.argument = argument; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public String getUnit_timeout() { return unit_timeout; }
    public void setUnit_timeout(String unit_timeout) { this.unit_timeout = unit_timeout; }
}
