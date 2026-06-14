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
package org.tweetyproject.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Oleksandr Dzhychko
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerCausalTest {
    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void causalReasonerWithInvalidKnowledgeBaseReturnsStatus400() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_conclusions",
                          "kb": "a <=> (b\\nc <=> d\\n{ d, !b }",
                          "observations": "!a, !b",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void causalReasonerWithInvalidObservationsReturnsStatus400() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_conclusions",
                          "kb": "a <=> b\\nc <=> d\\n{ d, !b }",
                          "observations": "!(a, !b",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void causalReasonerRepliesWithAllConclusions() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_conclusions",
                          "kb": "a <=> b\\nc <=> d\\n{ d, !b }",
                          "observations": "!a, !b",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "reply": "[!a, !b, c, d]",
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void causalReasonerRepliesWithFilteredConclusions() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_conclusions",
                          "kb": "a <=> b\\nc <=> d\\n{ d, !b }",
                          "observations": "!a, !b",
                          "conclusionsFilter": "b, c, e",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "reply": "[!b, c]",
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void causalReasonerCalculatesSignificantAtoms() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_significant_atoms",
                          "kb": "a <=> b\\nc <=> d\\n{ d, !b }",
                          "observations": "!a, !b",
                          "conclusionsFilter": "a",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "reply": "{\\n  \\"a\\" : [ \\"a\\", \\"b\\" ]\\n}",
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void causalReasonerGetSequenceExplanations() throws Exception {
        var post = post("/causal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "aId",
                          "cmd": "get_sequence_explanations",
                          "kb": "a <=> b\\n{ b, !b }",
                          "observations": "",
                          "conclusionsFilter": "a",
                          "timeout": 10,
                          "unit_timeout": "s"
                        }
                        """);


        var expectedReplyJSON = """
                {
                  "attacks" : [ {
                    "attacker" : "([b] -> b)",
                    "attacked" : "([!b] -> !a)"
                  }, {
                    "attacker" : "([!b] -> !b)",
                    "attacked" : "([b] -> b)"
                  }, {
                    "attacker" : "([b] -> b)",
                    "attacked" : "([!b] -> !b)"
                  }, {
                    "attacker" : "([!b] -> !b)",
                    "attacked" : "([b] -> a)"
                  } ],
                  "perAtomSequenceExplanations" : {
                    "a" : [ {
                      "argument" : "([b] -> a)",
                      "supporters" : [ [ "([b] -> b)" ], [ "([b] -> a)" ] ],
                      "defeated" : [ [ "([!b] -> !b)" ], [ ] ]
                    } ]
                  }
                }
                """;
        var expectedReplyJSONEscaped = objectMapper.writeValueAsString(expectedReplyJSON.stripTrailing());
        var expectedResponse = String.format("""
                {
                  "reply": %s,
                  "email": "aId",
                  "unit_timeout": "s",
                  "status": "SUCCESS"
                }
                """, expectedReplyJSONEscaped);
        mvc.perform(post)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse, false));
    }
}