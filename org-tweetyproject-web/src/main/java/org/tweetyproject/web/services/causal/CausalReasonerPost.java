/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.causal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * Request to execute a {@link CausalReasonerPost#cmd} with a {@link org.tweetyproject.causal.reasoner.AbstractCausalReasoner}
 *
 * @author Oleksandr Dzhychko
 */
public final class CausalReasonerPost {

    /**
     * Initialize new CausalReasonerPost
     * @param cmd               the command string
     * @param email             user email
     * @param kb                the causal knowledge base
     * @param observations      the observations
     * @param conclusionsFilter conclusion filter approach
     * @param timeout           the timeout
     * @param unit_timeout      unit of timeout
     */
    public CausalReasonerPost(
            @JsonProperty(value = "cmd", required = true)
            Cmd cmd,
            @JsonProperty("email")
            String email,
            @JsonProperty(value = "kb", required = true)
            String kb,
            @JsonProperty(value = "observations", required = true)
            String observations,
            @JsonProperty(value = "conclusions_filter")
            String conclusionsFilter,
            @JsonProperty(value = "timeout", required = true)
            int timeout,
            @JsonProperty(value = "unit_timeout", required = true)
            String unit_timeout
    ) {
        this.cmd = cmd;
        this.email = email;
        this.kb = kb;
        this.observations = observations;
        this.conclusionsFilter = conclusionsFilter;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
    }

    /**
     * Describes which command should be executed by the causal reasoner
     */
    public enum Cmd {
        /**
         * Instructs the reasoner to calculate the conclusions
         *
         * @see org.tweetyproject.causal.reasoner.AbstractCausalReasoner#getConclusions
         */
        @JsonProperty("get_conclusions") GET_CONCLUSIONS,

        /**
         * Instructs the reasoner to calculate per atom the atoms which are significant for its conclusion.
         */
        @JsonProperty("get_significant_atoms") GET_SIGNIFICANT_ATOMS,

        /**
         * Instructs the reasoner to calculate the corresponding argumentation framework.
         */
        @JsonProperty("get_argumentation_framework") GET_ARGUMENTATION_FRAMEWORK,

        /**
         * Instructs the reasoner to calculate the sequence of explanations for all consequences.
         */
        @JsonProperty("get_sequence_explanations") GET_SEQUENCE_EXPLANATIONS;
    }

    /**
     * The command type for the reasoner request
     */
    @NotNull
    private Cmd cmd;

    /**
     * The email associated with the request
     */
    @Nullable
    private String email;

    /**
     * The knowledge base (KB) for the reasoner request
     * The format of the knowledge base must be as described in {@link org.tweetyproject.causal.parser.CausalParser#parseBeliefBase(java.io.Reader)}
     */
    @NotNull
    private String kb;

    /**
     * The observations for the reasoner request
     * The format of the knowledge base must be as used by {@link org.tweetyproject.causal.parser.CausalParser#parseListOfFormulae} with "," (comma) as delimiter.
     */
    @NotNull
    private String observations;

    /**
     * Atoms for which the conclusions should be queried and returned.
     * The format of the knowledge base must be as described in {@link ConclusionsFilterSerialization#parse(String)}
     */
    @Nullable
    private String conclusionsFilter;

    /**
     * The timeout in seconds for the reasoner request
     */
    private int timeout;

    /**
     * The unit timeout for the reasoner request
     */
    @NotNull
    private String unit_timeout;

    public Cmd getCmd() {
        return this.cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getObservations() {
        return this.observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getConclusionsFilter() {
        return this.conclusionsFilter;
    }

    public void setConclusionsFilter(String conclusionsFilter) {
        this.conclusionsFilter = conclusionsFilter;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUnit_timeout() {
        return this.unit_timeout;
    }

    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }
}