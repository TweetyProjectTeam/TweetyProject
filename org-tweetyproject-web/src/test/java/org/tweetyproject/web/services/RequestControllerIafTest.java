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
class RequestControllerIafTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getInfos() throws Exception {
        var post = post("/iaf").contentType(MediaType.APPLICATION_JSON)
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
                            "get_models_pos",
                            "get_credulous_pos",
                            "get_skeptical_pos",
                            "get_models_nec",
                            "get_credulous_nec",
                            "get_skeptical_nec"
                          ]
                        }
                        """, true));
    }

    @Test
    public void getModels() throws Exception {
        var post = post("/iaf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_models_pos",
                           "nr_of_arguments": 4,
                           "uncertainArguments": [4],
                           "definiteAttacks": [[1, 2],[2,3],[4,4]],
                           "uncertainAttacks": [[2,1]],
                           "semantics": "ADM",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_models_pos",
                          "email": null,
                          "nr_of_arguments": 4,
                          "uncertainArguments": [4],
                          "definiteAttacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              4,
                              4
                            ]
                          ],
                          "uncertainAttacks": [
                            [
                              2,
                              1
                            ]
                          ],
                          "semantics": "ADM",
                          "solver": null,
                          "answer": "[{1}, {2}, {1,3}, {}]",
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void getCredulous() throws Exception {
        var post = post("/iaf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_credulous_pos",
                           "nr_of_arguments": 4,
                           "uncertainArguments": [4],
                           "definiteAttacks": [[1, 2],[2,3],[4,4]],
                           "uncertainAttacks": [[2,1]],
                           "semantics": "ADM",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_credulous_pos",
                          "email": null,
                          "nr_of_arguments": 4,
                          "uncertainArguments": [4],
                          "definiteAttacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              4,
                              4
                            ]
                          ],
                          "uncertainAttacks": [
                            [
                              2,
                              1
                            ]
                          ],
                          "semantics": "ADM",
                          "solver": null,
                          "answer": "{1,2,3}",
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void getSkeptical() throws Exception {
        var post = post("/iaf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_skeptical_pos",
                           "nr_of_arguments": 4,
                           "uncertainArguments": [4],
                           "definiteAttacks": [[1, 2],[2,3],[4,4]],
                           "uncertainAttacks": [[2,1]],
                           "semantics": "ADM",
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
                          "nr_of_arguments": 4,
                          "uncertainArguments": [4],
                          "definiteAttacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              4,
                              4
                            ]
                          ],
                          "uncertainAttacks": [
                            [
                              2,
                              1
                            ]
                          ],
                          "semantics": "ADM",
                          "solver": null,
                          "answer": "{}",
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
        var post = post("/iaf").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content(String.format("""
                         {
                           "cmd": "get_models_pos",
                           "nr_of_arguments": 4,
                           "uncertainArguments": [4],
                           "definiteAttacks": [[1, 2],[2, 3],[4,4]],
                           "uncertainAttacks": [[2,1]],
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