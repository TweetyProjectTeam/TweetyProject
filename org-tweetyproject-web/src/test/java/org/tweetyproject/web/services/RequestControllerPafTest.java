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
 * Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.web.services.bipolar.BipolarSemantics;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Lars Bengel
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerPafTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getInfos() throws Exception {
        var post = post("/paf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                        {
                          "cmd": "info"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "info",
                          "email": null,
                          "backend_timeout": 600,
                          "semantics": [
                            "CF","ADM","CO","GR","PR","ST","STG","STG2","SST","ID","EA","CF2","SCF2","NA","SAD","IS","UC","UD","SUD","WAD","WCO","WPR","WGR","div"
                          ],
                          "commands": [
                            "get_credulous",
                            "get_skeptical"
                          ],
                          "solvers": [
                            "simple",
                            "montecarlo"
                          ]
                        }
                        """, true));
    }

    @Test
    public void getCredulous() throws Exception {
        var post = post("/paf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_credulous",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2],[2,3]],
                           "semantics": "ADM",
                           "argument_probabilities": [1, 0.5, 1],
                           "attack_probabilities": [0.5, 1.0],
                           "solver": "simple",
                           "timeout": 10,
                           "unit_timeout": "ms"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_credulous",
                          "email": null,
                          "nr_of_arguments": 3,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ]
                          ],
                          "semantics": "ADM",
                          "argument_probabilities": [1.0, 0.5, 1.0],
                          "attack_probabilities": [0.5, 1.0],
                          "solver": "simple",
                          "answer": "{1=1.0, 2=0.25, 3=0.75}",
                          "unit_time": "ms",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void getSkeptical() throws Exception {
        var post = post("/paf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_skeptical",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2],[2,3]],
                           "semantics": "PR",
                           "argument_probabilities": [1, 0.5, 1],
                           "attack_probabilities": [0.5, 1],
                           "solver": "simple",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_skeptical",
                          "email": null,
                          "nr_of_arguments": 3,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ]
                          ],
                          "semantics": "PR",
                          "argument_probabilities": [1.0, 0.5, 1.0],
                          "attack_probabilities": [0.5, 1.0],
                          "solver": "simple",
                          "answer": "{1=1.0, 2=0.25, 3=0.75}",
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    private static Stream<Semantics> availableSemantics() {
        return Stream.of(Semantics.values());
    }

    @ParameterizedTest(name = "semantics {0}")
    @MethodSource("availableSemantics")
    public void getModelsForSemantics(Semantics semantics) throws Exception {
        if (semantics.equals(Semantics.diverse)) return;
        var post = post("/paf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content(String.format("""
                         {
                           "cmd": "get_credulous",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2],[2, 3]],
                           "argument_probabilities": [1, 0.5, 1],
                           "attack_probabilities": [1, 0.5],
                           "solver": "simple",
                           "semantics": "%s",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """, semantics.abbreviation()));

        mvc.perform(post).andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "status": "SUCCESS"
                    }
                    """));
    }
}