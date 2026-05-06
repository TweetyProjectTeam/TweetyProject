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
import org.tweetyproject.web.services.bipolar.BipolarSemantics;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Oleksandr Dzhychko
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerBipolarTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getInfos() throws Exception {
        var post = post("/bipolar").contentType(MediaType.APPLICATION_JSON)
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
                            "cf",
                            "sa",
                            "cl",
                            "c-ad",
                            "d-ad",
                            "n-ad",
                            "n-co",
                            "n-gr",
                            "n-pr",
                            "n-st"
                          ],
                          "commands": [
                            "get_models",
                            "get_model"
                          ]
                        }
                        """, true));
    }

    @Test
    public void getModels() throws Exception {
        var post = post("/bipolar").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_models",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2]],
                           "supports": [[2, 3]],
                           "semantics": "c-ad",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_models",
                          "email": null,
                          "nr_of_arguments": 3,
                          "attacks": [
                            [
                              1,
                              2
                            ]
                          ],
                          "supports": [
                            [
                              2,
                              3
                            ]
                          ],
                          "semantics": "c-ad",
                          "solver": null,
                          "answer": "[{1}, {3}, {1,3}, {2,3}]",
                          "time": 0.0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    @Test
    public void getModel() throws Exception {
        var post = post("/bipolar").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_model",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2]],
                           "supports": [[2, 3]],
                           "semantics": "c-ad",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_model",
                          "email": null,
                          "nr_of_arguments": 3,
                          "attacks": [
                            [
                              1,
                              2
                            ]
                          ],
                          "supports": [
                            [
                              2,
                              3
                            ]
                          ],
                          "semantics": "c-ad",
                          "solver": null,
                          "answer": "{1}",
                          "time": 0.0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    private static Stream<BipolarSemantics> availableSemantics() {
        return Stream.of(BipolarSemantics.values());
    }

    @ParameterizedTest(name = "semantics {0}")
    @MethodSource("availableSemantics")
    public void getModelsForSemantics(BipolarSemantics semantics) throws Exception {
        var post = post("/bipolar").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content(String.format("""
                         {
                           "cmd": "get_models",
                           "nr_of_arguments": 3,
                           "attacks": [[1, 2]],
                           "supports": [[2, 3]],
                           "semantics": "%s",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """, semantics.id));

        mvc.perform(post).andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "status": "SUCCESS"
                    }
                    """));
    }
}